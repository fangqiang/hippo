package pers.fq.hippo.biz;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fq.hippo.RequestHandlerImpl;
import pers.fq.hippo.common.ConsistHashWithVirtualNode;
import pers.fq.hippo.common.Utils;
import pers.fq.hippo.common.ZKClient;
import pers.fq.hippo.storage.impl.CacheImpl;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/12/3
 */
public class ClusterManager {

    private static final Logger logger = LoggerFactory.getLogger(ClusterManager.class);

    ZKClient zkClient;

    Set<String> servers = new HashSet<>();

    CacheImpl cache;

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    public ClusterManager(String zk, String zkPath, CacheImpl cache) throws Exception{
        zkClient = new ZKClient(zk, zkPath, this::handleNewServerList);
        zkClient.regist(Utils.getLocalIp());

        servers.addAll(zkClient.getServerIps());
        this.cache = cache;
    }

    private synchronized void handleNewServerList(Set<String> newServers){
        // 异步处理消息，防止阻塞zk通知
        executorService.submit(()->{
            if(!(this.servers.size() == newServers.size() && this.servers.containsAll(newServers))){
                cleanDataNotBelongMe(newServers);
                this.servers = newServers;
            }
        });
    }

    private void cleanDataNotBelongMe(Set<String> newServers){
        String localIP = Utils.getLocalIp();

        TreeMap<Integer, String> integerStringTreeMap = ConsistHashWithVirtualNode.hashWithVirtualNode(newServers);
        ConcurrentMap<String, Integer> map = cache.getIndex().getMap();

        AtomicInteger count = new AtomicInteger(0);
        map.forEach((k,v)->{
            if( ! localIP.equals(ConsistHashWithVirtualNode.get(integerStringTreeMap, k))){
                cache.remove(k);
                count.incrementAndGet();
            }
        });

        logger.info("rehash, clean data size: {}", count.get());
    }

    public void close() {
        zkClient.close();
    }
}

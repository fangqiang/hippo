package pers.fq.hippo.common;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/12/1
 */
public class ZKClient implements PathChildrenCacheListener{

    private static final Logger logger = LoggerFactory.getLogger(ZKClient.class);

    CuratorFramework client;

    String zk;
    String zkPath;

    final Consumer<Set<String>> consumer;

    public ZKClient(String zk, String zkPath, Consumer<Set<String>> consumer) throws Exception {

        this.zk = zk;
        this.zkPath = zkPath;
        this.consumer = consumer;

        // 1.Connect to zk
        client = CuratorFrameworkFactory.newClient(zk, new RetryNTimes(10, 5000));
        client.start();
        logger.info("zk client start successfully!");

        Stat stat = client.checkExists().forPath(zkPath);
        if(stat == null){
            client.create().forPath(zkPath);
            logger.info("init zkPath: {}", zkPath);
        }

        PathChildrenCache cache = new PathChildrenCache(client, zkPath, true);
        cache.start();
        cache.getListenable().addListener(this);
    }

    public void regist(String ip) {
        String node = zkPath + "/" + ip;
        recreate(client, node);
    }

    public Set<String> getServerIps() throws Exception {
        List<String> servers = client.getChildren().forPath(zkPath);

        if(servers == null || servers.size()==0){
            return new HashSet<>();
        }else {
            return new HashSet<>(servers);
        }
    }

    public Map<String,Long> getServers() throws Exception {
        Set<String> servers = getServerIps();

        Map<String,Long> ret = new HashMap<>();
        for (String server : servers) {
            long time = Long.parseLong(new String(client.getData().forPath(zkPath + "/" + server)));
            ret.put(server, time);
        }
        return ret;
    }

    public void close(){
        // 主动关闭，否则需要等待session 超时才会删除节点
        client.close();
    }

    /**
     * 先删除旧节点，然后重建新节点
     */
    private static void recreate(CuratorFramework client, String path) {
        try {
            client.delete().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
        Set<String> serverIps = getServerIps();
        logger.warn("server change, new cluster: {}", serverIps);
        consumer.accept(serverIps);
    }
}

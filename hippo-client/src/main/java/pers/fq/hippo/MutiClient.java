package pers.fq.hippo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fq.hippo.common.Assert;
import pers.fq.hippo.common.ConsistHashWithVirtualNode;
import pers.fq.hippo.common.ZKClient;
import pers.fq.hippo.transporter.NettyClientWithHeartBeat;
import pers.fq.hippo.transporter.Request;
import pers.fq.hippo.transporter.ResponseFuture;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 客户端入口，管理集群的客户端
 *
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/29
 */
class MutiClient {

    private static final Logger logger = LoggerFactory.getLogger(MutiClient.class);

    static final RuntimeException CONN_ERROR = new RuntimeException("connection error");

    private Set<String> servers;

    private Map<String,NettyClientWithHeartBeat> ipClient = new HashMap<>();

    private TreeMap<Integer,NettyClientWithHeartBeat> hashClient = new TreeMap<>();

    private int port;

    ZKClient zkClient;

    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    public MutiClient(String zk, String zkPath, int port) throws Exception {
        this.port = port;

        zkClient = new ZKClient(zk, zkPath, this::serverChange);

        // 第一次获取服务器ip，并初始化
        Set<String> serverIps = zkClient.getServerIps();
        updateRouter(serverIps);
    }

    private void updateRouter(Set<String> newServers){
        // 初始化各个连接
        Map<String, NettyClientWithHeartBeat> tmpIpClient = new HashMap<>(newServers.size());
        for (String server : newServers) {
            if(this.ipClient.containsKey(server)) {
                tmpIpClient.put(server, this.ipClient.get(server));
            }else{
                logger.warn("detect a new server: {}", server);
                tmpIpClient.put(server, createAndConnect(server));
            }
        }

        TreeMap<Integer, String> hashMap = ConsistHashWithVirtualNode.hashWithVirtualNode(newServers);

        TreeMap<Integer, NettyClientWithHeartBeat> tmpHashClient = new TreeMap<>();
        hashMap.forEach((k,v)->{
            tmpHashClient.put(k, tmpIpClient.get(v));
        });

        Set<NettyClientWithHeartBeat> oldClients = new HashSet<>(ipClient.values());
        Set<NettyClientWithHeartBeat> newClients = new HashSet<>(tmpIpClient.values());

        this.servers = newServers;
        this.ipClient = tmpIpClient;
        this.hashClient = tmpHashClient;

        // 关闭不再使用的连接
        oldClients.removeAll(newClients);
        for (NettyClientWithHeartBeat oldClient : oldClients) {
            logger.warn("close connection, remote: {}", oldClient.getChannel().remoteAddress());
            oldClient.close();
        }
    }

    /**
     * 同步处理变更
     */
    private synchronized void serverChange(Set<String> set) {
        executorService.submit(()-> {
            logger.info("server changed: new cluster: {}, old cluster: {}", set, servers);
            if (!(this.servers.size() == set.size() && this.servers.containsAll(set))) {
                updateRouter(set);
            }
        });
    }

    /**
     * 连接可能失败，后台任务会定期检测，一旦心跳联通，则恢复连接
     */
    public NettyClientWithHeartBeat createAndConnect(String ip){
        NettyClientWithHeartBeat client = new NettyClientWithHeartBeat(ip, port);
        client.openAndConnect();
        return client;
    }

    /**
     * 选择一个客户端
     *
     * @param request
     * @return 返回一个Future， 如果连接断开则返回空
     */
    protected ResponseFuture send(Request request) {
        Assert.check(hashClient.size() > 0, "no server available");

        NettyClientWithHeartBeat client = ConsistHashWithVirtualNode.get(hashClient, request.getKey());

        logger.debug("send to client: {}", client.getChannel().remoteAddress());
        if(client.isActive()){
            return client.send(request);
        }else{
            throw CONN_ERROR;
        }
    }
}

package pers.fq.hippo;

import pers.fq.hippo.biz.ClusterManager;
import pers.fq.hippo.common.Utils;
import pers.fq.hippo.storage.impl.CacheImpl;
import pers.fq.hippo.storage.impl.Config;
import pers.fq.hippo.transporter.NettyServer;
import pers.fq.hippo.transporter.ServerConfig;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/14
 */

public class HippoServer {

    public void run(String zk, String zkPath, int port) throws Throwable {

        String ip = Utils.getLocalIp();

        // 初始化缓存
        CacheImpl store = new CacheImpl(Config.CACHE_MAX_SIZE, Config.CACHE_TTL);

        // 实例化请求处理器，连接网络、缓存
        RequestHandlerImpl requestHandler = new RequestHandlerImpl(store);

        // 初始化配置
        ServerConfig config = new ServerConfig(requestHandler, ip, port);

        // 初始化网络连接
        NettyServer server = new NettyServer(config);

        ClusterManager clusterManager = new ClusterManager(zk, zkPath, store);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            clusterManager.close();
            System.out.println("shutdown after 15s");
            Utils.sleep(15000);
            System.out.println("clean zk node");
        }));
    }
}

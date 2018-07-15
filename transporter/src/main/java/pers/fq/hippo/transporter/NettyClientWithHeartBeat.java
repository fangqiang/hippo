package pers.fq.hippo.transporter;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * NettyServer, 对每个连接进行心跳检测，直到这个连接被关闭
 * @author fang
 */
public class NettyClientWithHeartBeat extends NettyClient{

    private static final Logger logger = LoggerFactory.getLogger(NettyClientWithHeartBeat.class);

    private final static ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory("client-heartbeat", true));

    /**
     * 保存每个连接的最后心跳时间，超过一定时间则重连
     */
    private static ConcurrentMap<NettyClient, Long> clientMap = new ConcurrentHashMap();

    static {
        startCheckHeartbeatTask();
    }

    public NettyClientWithHeartBeat(String remoteIp, int remotePort) {
        super(remoteIp, remotePort);
        clientMap.put(this, System.currentTimeMillis());
    }

    @Override
    public void close(){
        clientMap.remove(this);
        super.close();
    }

    /**
     * 定时检测channel，指定时间内没有收到心跳，则关闭连接
     */
    private static void startCheckHeartbeatTask(){
        scheduled.scheduleAtFixedRate(()->{
            try{
                heartbeatDetect();
            }catch (Exception e){
                logger.error("scheduled heartbeat error", e);
            }
        }, Config.HEARTBEAT_INTERVAL_MS, Config.HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    private static void heartbeatDetect() {
        logger.info("send heartbeat");

        for (Map.Entry<NettyClient, Long> next : clientMap.entrySet()) {
            NettyClient client = next.getKey();
            Long lastSuccessfulTime = next.getValue();

            if (client.getChannel() == null) {
                client.reconnect();
                continue;
            }

            try {
                client.send(Request.getHeartbeatRequest()).get(3000, TimeUnit.MILLISECONDS);
                // 更新最后心跳成功时间
                next.setValue(System.currentTimeMillis());
            } catch (Exception e) {
                logger.error("heartbeat fail, remote " + client.remoteAddress(), e);

                if (System.currentTimeMillis() - lastSuccessfulTime > Config.HEARTBEAT_TIMEOUT) {
                    // 心跳一直没成功，尝试重连，重连依然可能失败
                    client.reconnect();
                    // 不管重连成功失败都更新最后时间，防止重连频率过高
                    next.setValue(System.currentTimeMillis());
                }
            }
        }
    }
}
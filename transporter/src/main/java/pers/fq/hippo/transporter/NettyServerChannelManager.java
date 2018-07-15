package pers.fq.hippo.transporter;

import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fq.hippo.common.monitor.Monitor;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 管理每个连接成功的channel，如果一定时间内没有收到心跳则关闭该连接
 * @author: fang
 * @date: Created by on 18/8/1
 */
public class NettyServerChannelManager {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerChannelManager.class);

    private static final ScheduledThreadPoolExecutor SCHEDULED = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory("server-check-heartbeat", true));

    private static final ConcurrentMap<Channel, Long> CHANNEL_MAP = new ConcurrentHashMap();

    static {
        startCheckHeartbeatTask();

        registMonitor();
    }

    static void add(Channel ch) {
        if (ch != null && ch.isActive()) {
            CHANNEL_MAP.put(ch, System.currentTimeMillis());
        }
    }

    static void remove(Channel ch) {
        if (ch != null && !ch.isActive()) {
            CHANNEL_MAP.remove(ch);
        }
    }

    static void updateTime(Channel channel){
        CHANNEL_MAP.put(channel, System.currentTimeMillis());
    }

    /**
     * 定时检测channel，指定时间内没有收到心跳，则关闭连接
     */
    private static void startCheckHeartbeatTask(){
        SCHEDULED.scheduleAtFixedRate(()->{
            try {
                checkAlive();
            }catch (Exception e){
                logger.error("checkHeartbeat failed: ", e);
            }

        }, Config.HEARTBEAT_INTERVAL_MS, Config.HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    private static void checkAlive() {
        Iterator<Map.Entry<Channel, Long>> it = CHANNEL_MAP.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Channel, Long> e = it.next();

            Channel channel = e.getKey();
            long lastHeartbeatTime = e.getValue();

            // 定时检测channel，指定时间内没有收到心跳，则关闭连接
            if (System.currentTimeMillis() - lastHeartbeatTime > Config.HEARTBEAT_TIMEOUT) {
                it.remove();
                channel.close();
            }
        }
    }

    private static void registMonitor(){
        Monitor.regist(() -> {
            Monitor.value("server_channel_size", CHANNEL_MAP.size());
        });
    }
}

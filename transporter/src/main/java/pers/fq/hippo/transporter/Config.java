package pers.fq.hippo.transporter;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/3
 */
public class Config {

    /**
     * 发送一次心跳的时间间隔，一定要比服务端的超时断开时间要段
     */
    public static final int HEARTBEAT_INTERVAL_MS = 10_000;
    public static final int HEARTBEAT_TIMEOUT = HEARTBEAT_INTERVAL_MS * 3;

    /**
     * netty最大一次传输10MB
     */
    public static final int MAX_BYTE = 1024 * 1024 * 10;
}

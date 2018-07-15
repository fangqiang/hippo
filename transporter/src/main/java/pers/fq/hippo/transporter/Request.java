package pers.fq.hippo.transporter;

import pers.fq.hippo.common.SerialUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/26
 */
public class Request implements Serializable {

    /**
     * 唯一请求id
     */
    protected long uuid;

    boolean isHeartbeat;

    /**
     * 哈希到对应的服务器
     */
    String key;

    /**
     * 请求类型,根据这个类型解析请求内容
     */
    protected int type;

    /**
     * 请求内容
     */
    protected byte [] data;

    private static final AtomicLong idxGenerator = new AtomicLong(1);

    /**
     *  为了使用kryo序列化
     */
    public Request() {}

    public Request(String key, byte [] data, int type) {
        this.key = key;
        this.data = data;
        this.type = type;
        uuid = idxGenerator.getAndIncrement();
    }

    static Request getHeartbeatRequest(){
        Request request = new Request();
        request.isHeartbeat = true;
        return request;
    }

    public byte[] toBytes(){
        return SerialUtils.obj2Byte(this);
    }

    public static Request parseFrom(byte[] data){
        return SerialUtils.byte2Obj(data, Request.class);
    }

    public long getUuid() {
        return uuid;
    }

    public byte[] getData(){
        return data;
    }

    public int getType() {
        return type;
    }

    public String getKey() {
        return key;
    }
}

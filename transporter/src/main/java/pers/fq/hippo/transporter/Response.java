package pers.fq.hippo.transporter;

import io.netty.channel.Channel;
import pers.fq.hippo.common.SerialUtils;

import java.io.Serializable;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/28
 */
public class Response implements Serializable {

    public long uuid;

    /**
     * 请求类型,根据这个类型解析请求内容
     */
    protected int type;

    byte[] result;

    Channel channel;

    /**
     *  为了使用kryo序列化
     */
    public Response() { }

    public Response(long uuid) {
        this.uuid = uuid;
    }

    public Response(long uuid, byte[] result) {
        this.uuid = uuid;
        this.result = result;
    }

    public long getUuid() {
        return uuid;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public byte[] getResult() {
        return result;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] toBytes(){
        return SerialUtils.obj2Byte(this);
    }

    public static Response parseFrom(byte[] data){
        return SerialUtils.byte2Obj(data, Response.class);
    }
}

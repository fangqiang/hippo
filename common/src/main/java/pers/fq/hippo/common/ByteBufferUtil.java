package pers.fq.hippo.common;

import pers.fq.hippo.common.tag.SideEffect;

import java.nio.ByteBuffer;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/22
 */
public class ByteBufferUtil {

    @SideEffect
    public static String getString(ByteBuffer buffer){
        return new String(getStringByte(buffer));
    }

    @SideEffect
    public static byte[] getStringByte(ByteBuffer buffer){
        int len = buffer.get();
        byte[]  v = new byte[len];
        buffer.get(v);
        return v;
    }

    @SideEffect
    public static void skipString(ByteBuffer buffer){
        int len = buffer.get();
        buffer.position(buffer.position()+len);
    }

    @SideEffect
    public static void putString(ByteBuffer buffer, String str){
        byte[] bytes = str.getBytes();
        putStringByte(buffer, bytes);
    }

    @SideEffect
    public static void putStringByte(ByteBuffer buffer, byte[] bytes){
        buffer.put((byte) bytes.length);
        buffer.put(bytes);
    }

    public static byte[] copyUsedArray(ByteBuffer buffer, int start, int len){
        byte [] ret = new byte[len];
        System.arraycopy(buffer.array(), start, ret, 0, len);
        return ret;
    }

    @SideEffect
    public static void skip(ByteBuffer buffer, int len){
        buffer.position(buffer.position()+len);
    }
}

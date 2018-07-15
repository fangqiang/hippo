package pers.fq.hippo.storage.bean;

import java.nio.ByteBuffer;

/**
 * @Description: 核心存储对象
 * @author: fang
 * @date: Created by on 18/8/18
 */
public class BeanDO {
    public final String key;
    public final byte[] value;
    public final long expireTime;


    // TODO 思考可以重用的ByteBuffer, 减少GC
    public BeanDO(String key, byte[] value, long expireTime) {
        this.expireTime = expireTime;
        this.key = key;
        this.value = value;
    }

    public int size() {
        return 16 + key.getBytes().length + value.length;
    }

    /**
     * 将bean实例化成byte []
     */
    public ByteBuffer toWritableByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(size());
        buffer.putLong(expireTime);
        byte[] bytes = key.getBytes();
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.flip();
        return buffer;
    }

    public static BeanDO parseBean(ByteBuffer byteBuffer) {
        long expireTime = byteBuffer.getLong();
        int keyLength = byteBuffer.getInt();
        byte[] key = new byte[keyLength];
        byteBuffer.get(key);
        int valueLength = byteBuffer.getInt();
        byte[] value = new byte[valueLength];
        byteBuffer.get(value);

        return new BeanDO(new String(key), value, expireTime);
    }
}

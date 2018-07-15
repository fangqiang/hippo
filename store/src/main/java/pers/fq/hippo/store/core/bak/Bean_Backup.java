//package pers.fq.hippo.store.core;
//
//import pers.fq.hippo.store.exp.BeanTooBig;
//import pers.fq.hippo.store.exp.HippoException;
//
//import java.nio.ByteBuffer;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/17
// */
//public class Bean_Backup {
//
//    /**
//     * 最后一次更新的时间，用于判断key过期
//     */
//    final long time;
//
//    /**
//     * key
//     */
//    final byte[] key;
//
//    /**
//     * List<Line> 序列化之后的数组
//     */
//    final byte[] value;
//
//    public static Bean_Backup instance(final byte[] value) throws BeanTooBig {
//        return instance(0L, new byte[0], value);
//    }
//
//    public static Bean_Backup instance(final long time, final byte[] key, final byte[] value) throws BeanTooBig {
//        if (size(key, value) <= Config.MAX_CHUNK_SIZE) {
//            return new Bean_Backup(time, key, value);
//        } else {
//            throw HippoException.BEAN_TOO_BIG;
//        }
//    }
//
//    private Bean_Backup(final long time, final byte[] key, final byte[] value) {
//        this.time = time;
//        this.key = key;
//        this.value = value;
//    }
//
//    public long getTime() {
//        return time;
//    }
//
//
//    public byte[] getKey() {
//        return key;
//    }
//
//    public byte[] getValue() {
//        return value;
//    }
//
//    public int size() {
//        return size(key, value);
//    }
//
//    public boolean isTerminateBean(){
//        return time == -1;
//    }
//
//    private static int size(final byte[] key, final byte[] value) {
//        return 13 + key.length + value.length;
//    }
//
//    /**
//     * 将bean实例化成byte []
//     */
//    public ByteBuffer toWritableByteBuffer() {
//        ByteBuffer buffer = ByteBuffer.allocate(size());
//        buffer.putLong(time);
//        buffer.put((byte) key.length);
//        buffer.put(key);
//        buffer.putInt(value.length);
//        buffer.put(value);
//        buffer.flip();
//        return buffer;
//    }
//
//    /**
//     * 从内存中取出对象
//     *
//     * @param byteBuffer
//     * @param offset
//     * @return
//     */
//    public static Bean_Backup parseBean(ByteBuffer byteBuffer, int offset) {
//        byteBuffer.position(offset);
//
//        long time = byteBuffer.getLong();
//
//        byte keyLength = byteBuffer.get();
//        byte[] key = new byte[keyLength];
//        byteBuffer.get(key);
//
//        int valueLength = byteBuffer.getInt();
//        byte[] value = new byte[valueLength];
//        byteBuffer.get(value);
//
//        return new Bean_Backup(time, key, value);
//    }
//
//    static class Meta {
//        final long time;
//        final byte[] key;
//
//        private Meta(long time, byte[] key) {
//            this.time = time;
//            this.key = key;
//        }
//    }
//
//    public static Meta parseMeta(ByteBuffer byteBuffer, int offset) {
//        byteBuffer.position(offset);
//
//        long time = byteBuffer.getLong();
//
//        byte keyLength = byteBuffer.get();
//        byte[] key = new byte[keyLength];
//        byteBuffer.get(key);
//
//        return new Meta(time, key);
//    }
//}

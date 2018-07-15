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
//public class Bean {
//
//    /**
//     * List<Line> 序列化之后的数组
//     */
//    final byte[] value;
//
//    public static Bean instance(final byte[] value) throws BeanTooBig {
//        if (size(value) <= Config.MAX_CHUNK_SIZE) {
//            return new Bean(value);
//        } else {
//            throw HippoException.BEAN_TOO_BIG;
//        }
//    }
//
//    private Bean(final byte[] value) {
//        this.value = value;
//    }
//
//
//    public byte[] getValue() {
//        return value;
//    }
//
//    public int size() {
//        return size(value);
//    }
//
//
//    private static int size(final byte[] value) {
//        return 4 + value.length;
//    }
//
//    /**
//     * 将bean实例化成byte []
//     */
//    public ByteBuffer toWritableByteBuffer() {
//        ByteBuffer buffer = ByteBuffer.allocate(size());
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
//    public static Bean parseBean(ByteBuffer byteBuffer, int offset) {
//        byteBuffer.position(offset);
//
//        int valueLength = byteBuffer.getInt();
//        byte[] value = new byte[valueLength];
//        byteBuffer.get(value);
//
//        return new Bean(value);
//    }
//}

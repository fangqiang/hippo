package pers.fq.hippo.store.idx;

import sun.misc.Unsafe;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/12
 */
public class SyncByte {

    public static Unsafe unsafe = UnsafeInstance.unsafe;

    private static final int byteBase = unsafe.arrayBaseOffset(byte[].class);

    public static byte getByte(byte[] array, int offset) {
        return unsafe.getByteVolatile(array, byteOffset(offset));
    }

    public static void setByte(byte[] array, int offset, byte newValue) {
        unsafe.putByteVolatile(array, byteOffset(offset), newValue);
    }

    private static long byteOffset(int i) {
        return ((long) i) + byteBase;
    }


//    public static void main(String[] args) {
//        byte [] a= {1,2,3};
//
//        System.out.println(getLong(a, 0));
//        System.out.println(getLong(a, 1));
//        System.out.println(getLong(a, 2));
//
//        setLong(a, 1, (byte) 6);
//        System.out.println(getLong(a, 1));
//    }
}

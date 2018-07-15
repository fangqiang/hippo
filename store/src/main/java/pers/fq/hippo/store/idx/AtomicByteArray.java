package pers.fq.hippo.store.idx;

import sun.misc.Unsafe;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/12
 */
public class AtomicByteArray {

    private final int[] array;

    public AtomicByteArray(int length) {
        array = new int[length];
    }

    public static Unsafe unsafe = UnsafeInstance.unsafe;

    private static final int byteBase = unsafe.arrayBaseOffset(byte[].class);

    private static long byteOffset(int i) {
        return ((long) i) + byteBase;
    }

    public byte get(int offset) {
        return unsafe.getByteVolatile(array, byteOffset(offset));
    }

    public void set(int offset, byte newValue) {
        unsafe.putByteVolatile(array, byteOffset(offset), newValue);
    }

    public final int length() {
        return array.length;
    }
}

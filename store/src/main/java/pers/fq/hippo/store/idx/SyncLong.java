package pers.fq.hippo.store.idx;

import sun.misc.Unsafe;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/12
 */
public class SyncLong {

    public static Unsafe unsafe = UnsafeInstance.unsafe;

    private static final int longBase = unsafe.arrayBaseOffset(long[].class);
    private static final int longShift;

    static {
        int scale = unsafe.arrayIndexScale(long[].class);
        if ((scale & (scale - 1)) != 0) {
            throw new Error("data leftType scale not a power of two");
        }
        longShift = 31 - Integer.numberOfLeadingZeros(scale);
    }

    public static long getLong(long[] array, int offset) {
        return unsafe.getLongVolatile(array, byteOffset(offset));
    }

    public static void setLong(long[] array, int offset, long newValue) {
        unsafe.putLongVolatile(array, byteOffset(offset), newValue);
    }

    private static long byteOffset(int i) {
        return ((long) i << longShift) + longBase;
    }
//
//
//    public static void main(String[] args) {
//        long [] a= {1,2,3};
//
//        System.out.println(getLong(a, 0));
//        System.out.println(getLong(a, 1));
//        System.out.println(getLong(a, 2));
//
//        setLong(a, 1, 4);
//        System.out.println(getLong(a, 1));
//    }
}

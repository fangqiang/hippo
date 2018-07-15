package pers.fq.hippo.store.idx;

import sun.misc.Unsafe;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/12
 */
public class SyncInt {

    public static Unsafe unsafe = UnsafeInstance.unsafe;

    private static final int intBase = unsafe.arrayBaseOffset(int[].class);
    private static final int intShift;

    static {
        int scale = unsafe.arrayIndexScale(int[].class);
        if ((scale & (scale - 1)) != 0) {
            throw new Error("data leftType scale not a power of two");
        }
        intShift = 31 - Integer.numberOfLeadingZeros(scale);
    }

    public static int getInt(int[] array, int offset) {
        return unsafe.getIntVolatile(array, byteOffset(offset));
    }

    public static void setInt(int[] array, int offset, int newValue) {
        unsafe.putIntVolatile(array, byteOffset(offset), newValue);
    }

    private static long byteOffset(int i) {
        return ((long) i << intShift) + intBase;
    }
//
//    public static void main(String[] args) {
//        int [] a= {1,2,3};
//
//        System.out.println(getInt(a, 0));
//        System.out.println(getInt(a, 1));
//        System.out.println(getInt(a, 2));
//
//        setInt(a, 1, 5);
//        System.out.println(getInt(a, 1));
//    }
}

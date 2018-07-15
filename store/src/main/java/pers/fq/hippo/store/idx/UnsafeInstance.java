package pers.fq.hippo.store.idx;

import java.lang.reflect.Field;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/12
 */
public class UnsafeInstance {

    public static sun.misc.Unsafe unsafe;

    static {
        try {
            Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (sun.misc.Unsafe)theUnsafe.get(null);
        } catch (Exception e) {
        }
    }
}

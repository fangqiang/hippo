package pers.fq.hippo.storage;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/11/16
 */
final public class SlabOffsetUtil {
    /**
     * @param slab 高4位用于表示slab id
     * @param offset 低28位用于表示slab内的偏移量
     * @return
     */
    public static int combine(int slab, int offset){
        return (slab << 28) | offset;
    }

    /**
     * 高4位用于表示slab id
     *
     * @param
     * @return
     */
    public static int getSlab(int index){
        return index >>> 28;
    }

    /**
     * 低28位用于表示slab内的偏移量
     *
     * @param index
     * @return
     */
    public static int getOffset(int index){
        return index & 0x0fff_ffff;
    }
}

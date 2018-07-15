//package pers.fq.hippo.storage;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/11/16
// */
//public class IndexUtilBak {
//    public static long combine(long time, long slab, long offset){
//        return (time << 32) | (slab << 28) | offset;
//    }
//
//    public static long getTime(long index){
//        return index >>> 32;
//    }
//
//    public static long getSlab(long index){
//        return (index >>> 28) & 0x0000_0000_0000_000f;
//    }
//
//    public static long getOffset(long index){
//        return index & 0x0000_0000_0fff_ffffL;
//    }
//
//    public static void main(String[] args) {
//        long index = combine(1542355446, 13, 268435455);
//        System.out.println(index);
//        System.out.println(getTime(index));
//        System.out.println(getSlab(index));
//        System.out.println(getOffset(index));
//    }
//}

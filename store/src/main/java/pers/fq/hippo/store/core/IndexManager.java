//package pers.fq.hippo.store.core;
//
//import pers.fq.hippo.store.tag.Nullable;
//import pers.fq.hippo.store.tag.ThreadSafe;
//
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/17
// */
//@ThreadSafe
//public class IndexManager {
//
//    /**
//     * key实例, value实例一旦创建就不可更改，直到key被删除、value被gc回收
//     */
//    public static ConcurrentHashMap<String, byte[]> map = new ConcurrentHashMap();
//
//    public static void remove(final String key) {
//        map.remove(key);
//    }
//
//    public static void put(final String key, final IdxValue idxValue) {
//        map.put(key, idxValue.idx);
//    }
//
//    @Nullable
//    public static IdxValue getIdx(final String key) {
//        byte[] v = map.get(key);
//
//        if (v == null) {
//            return null;
//        } else {
//            return IdxValue.parseIdx(v);
//        }
//    }
//}

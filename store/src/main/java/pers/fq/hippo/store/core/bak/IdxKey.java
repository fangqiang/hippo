//package pers.fq.hippo.store.core;
//
//import pers.fq.hippo.common.ByteUtil;
//
//import java.util.Arrays;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/8/4
// */
//public class IdxKey implements Comparable<IdxKey>{
//
//    public final byte[] key;
//    public final long lastUpdateTime;
//
//    private IdxKey(byte[] key, long lastUpdateTime) {
//        this.key = key;
//        this.lastUpdateTime = lastUpdateTime;
//    }
//
//
//    @Override
//    public int compareTo(IdxKey o) {
//        // key相等，直接返回
//        int c = ByteUtil.compareTo(key, o.key);
////        if(c == 0){
////            return 0;
////        }
//
//        if(lastUpdateTime > o.lastUpdateTime){
//            return 1;
//        }else if( lastUpdateTime < o.lastUpdateTime){
//            return -1;
//        }else{
//            return c;
//        }
//    }
//
//    @Override
//    public boolean equals(Object obj){
//        return Arrays.equals(key, ((IdxKey)obj).key);
//    }
//
//    @Override
//    public int hashCode(){
//        return Arrays.hashCode(key);
//    }
//
//    /**
//     * @param key
//     * @return
//     */
//    public static IdxKey instanceKeyOnly(byte[] key){
//        return new IdxKey(key, 0L);
//    }
//
//    public static IdxKey instanceKeyAndTime(byte[] key){
//        return new IdxKey(key, System.currentTimeMillis());
//    }
//
//    public static IdxKey instanceKeyAndTime(byte[] key, long time){
//        return new IdxKey(key, time);
//    }
//}

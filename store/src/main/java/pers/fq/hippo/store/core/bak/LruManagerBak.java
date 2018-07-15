//package pers.fq.hippo.store.core.bak;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import pers.fq.hippo.store.core.Config;
//import pers.fq.hippo.store.core.IdxKey;
//import pers.fq.hippo.store.tag.Nullable;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.concurrent.ConcurrentSkipListSet;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/8/4
// */
//public class LruManagerBak {
//
//    private static final Logger logger = LoggerFactory.getLogger(LruManagerBak.class);
//
//    final ConcurrentSkipListSet<IdxKey> set = new ConcurrentSkipListSet();
//
//    public void add(IdxKey key){
//        set.add(key);
//    }
//
//    public void remove(IdxKey key){
//        set.remove(key);
//    }
//
//    public void update(IdxKey oldKey, IdxKey newKey){
//        // 必须要先删除相同的key，否则可能存在2个相同的key
//        set.remove(oldKey);
//        set.add(newKey);
//    }
//
//    @Nullable
//    public IdxKey getOldest(){
//        return set.pollFirst();
//    }
//
//    public synchronized List<IdxKey> getOldestKeys(){
//        try {
//            List<IdxKey> list = new ArrayList<>();
//
//
//            while(true) {
//
//                IdxKey key = set.first(); // 成功，或者抛出异常
//
//                boolean expired = System.currentTimeMillis() - key.lastUpdateTime > Config.TTL;
//
//                if(!expired){
//                    break;
//                }else{
//                    key = getOldest();
//
//                    if(key!=null) {
//                        list.add(key);
//                    }else{
//                        // 因为该函数是同步的，所以不存在first成功，pollFirst失败
//                        logger.error("should never be here");
//                        break;
//                    }
//                }
//            }
//
//            return list;
//
//        }catch (NoSuchElementException e){
//            return Collections.EMPTY_LIST;
//        }catch (Exception e){
//            logger.error("", e);
//            return Collections.EMPTY_LIST;
//        }
//    }
//}

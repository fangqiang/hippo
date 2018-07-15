//package pers.fq.hippo.store.core;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/21
// */
//public class LockPool {
//
//    private static final Logger logger = LoggerFactory.getLogger(LockPool.class);
//
//    static final Map<Integer, byte[]> keyLock = new HashMap<>(Config.LOCK_SIZE);
//
//    static {
//        for (int i = 0; i < Config.LOCK_SIZE; i++) {
//            keyLock.put(i, new byte[0]);
//        }
//        logger.info("init LockPool size: {}", Config.LOCK_SIZE);
//    }
//
//    static Object getLock(String key) {
//        int idx = Math.abs(key.hashCode()) % Config.LOCK_SIZE;
//        return keyLock.get(idx);
//    }
//}

//package pers.fq.hippo.store.core;
//
//import org.testng.Assert;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/8/4
// */
//public class ConfigTest {
//
//    StoreImpl store;
//
//    @BeforeMethod
//    public void beforeTest() {
//        Config.MIN_CHUNK_SIZE = 128;
//        Config.MAX_CHUNK_SIZE = 1024;
//        Config.MAX_OFF_HEAP_SIZE = 1024 * 100;
//        Config.PAGE_SIZE = 1024 * 10;
//
//        Config.EXPIRE_CLEAN_PERIOD_MS = 1_000;
//        Config.TTL = 10_000;
//
//        PageManager.allocatedMemory = 0L;
//
//        store = new StoreImpl(null);
//
//        IndexManager.map.clear();
//        PageManager.freePages.clear();
//    }
//}
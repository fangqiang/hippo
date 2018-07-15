//package pers.fq.hippo.store.recover;
//
//import org.testng.Assert;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//import pers.fq.hippo.store.core.*;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/25
// */
//public class PersistImageAsyncTest {
//
//
//    SlabManager slabManager;
//
//    @BeforeMethod
//    public void beforeTest() {
//        Config.MIN_CHUNK_SIZE = 128;
//        Config.MAX_CHUNK_SIZE = 1024;
//        Config.MAX_OFF_HEAP_SIZE = 1024 * 100;
//        Config.PAGE_SIZE = 1024 * 10;
//
//        Config.FRAGMENT_CLEAN_PERIOD_MS = 10000_000;
//        Config.EXPIRE_CLEAN_PERIOD_MS = 3000_000;
//
//        PageManager.allocatedMemory = 0L;
//
//        slabManager = new SlabManager(null);
//
//        IndexManager.map.clear();
//        PageManager.freePages.clear();
//    }
//
//    @Test
//    public void testStart() throws Exception {
//        slabManager.put("a1", Bean.instanceKeyAndTime(1L, "a1".getBytes(), "111".getBytes()));
//        slabManager.put("a2", Bean.instanceKeyAndTime(1L, "a2".getBytes(), "111".getBytes()));
//        slabManager.put("a3", Bean.instanceKeyAndTime(1L, "a3".getBytes(), "111".getBytes()));
//
//        PersistImageAsync p = new PersistImageAsync(slabManager);
//
//        Thread.sleep(1000);
//
//        Bean bean10 = slabManager.get("a1");
//        Assert.assertEquals(new String(bean10.getKey()), "a1");
//        Bean bean20 = slabManager.get("a2");
//        Assert.assertEquals(new String(bean20.getKey()), "a2");
//        Bean bean30 = slabManager.get("a3");
//        Assert.assertEquals(new String(bean30.getKey()), "a3");
//
//
//        slabManager.remove("a1");
//        slabManager.remove("a2");
//        slabManager.remove("a3");
//
//        Bean bean1 = slabManager.get("a1");
//        Assert.assertEquals(bean1, null);
//        Bean bean2 = slabManager.get("a2");
//        Assert.assertEquals(bean2, null);
//        Bean bean3 = slabManager.get("a3");
//        Assert.assertEquals(bean3, null);
//
//        LoaderImageSync loaderImageSync = new LoaderImageSync(slabManager);
//
//        Thread.sleep(2000);
//
//        Bean bean11 = slabManager.get("a1");
//        Assert.assertEquals(new String(bean11.getKey()), "a1");
//        Bean bean21 = slabManager.get("a2");
//        Assert.assertEquals(new String(bean21.getKey()), "a2");
//        Bean bean31 = slabManager.get("a3");
//        Assert.assertEquals(new String(bean31.getKey()), "a3");
//    }
//}
//package pers.fq.hippo.store.core;
//
//import org.testng.Assert;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//
//import static org.testng.Assert.*;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/8/4
// */
//public class StoreImplTest extends ConfigTest{
//
//    @BeforeMethod
//    public void beforeTest() {
//        super.beforeTest();
//
//        Config.TTL = 10000_000;
//    }
//
//    @Test
//    public void testGet() throws Exception {
//        store.put("a", Bean.instance("va".getBytes()));
//        Assert.assertEquals(new String(store.get("a").getValue()), "va");
//    }
//
//    @Test
//    public void testPut() throws Exception {
//        store.put("a", Bean.instance(new byte[130]));
//    }
//
//    @Test
//    public void testRemove() throws Exception {
//        store.put("a", Bean.instance("va".getBytes()));
//        Assert.assertEquals(new String(store.get("a").getValue()), "va");
//        store.remove("a");
//        Assert.assertEquals(store.get("a"), null);
//    }
//
//    @Test
//    public void testAppend() throws Exception {
//    }
//
//    @Test
//    public void testOverWrite() throws Exception {
//        long size = Config.MAX_OFF_HEAP_SIZE / Config.MIN_CHUNK_SIZE;
//
//        // 内存最多存下这么多
//        for (int i = 0; i < size; i++) {
//            store.put(i+"", Bean.instance("va".getBytes()));
//        }
//
//        // 再存一个
//        store.put("a", Bean.instance("va".getBytes()));
//    }
//
//    @Test
//    public void testUpgrade() throws Exception {
//        store.put("a", Bean.instance("va".getBytes()));
//        store.put("a", Bean.instance("vb".getBytes()));
//        Assert.assertEquals(store.get("a").value, "vb".getBytes());
//
//        store.put("a", Bean.instance(new byte[130]));
//        Assert.assertEquals(store.get("a").value, new byte[130]);
//    }
//}
//package pers.fq.hippo.store.core;
//
//import org.testng.Assert;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/19
// */
//public class SlabManagerTest {
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
//        Config.EXPIRE_CLEAN_PERIOD_MS = 30000_000;
//
//        PageManager.allocatedMemory = 0L;
//
//        slabManager = new SlabManager(beanMerge);
//
//        IndexManager.map.clear();
//        PageManager.freePages.clear();
//    }
//
//    @Test
//    public void testStore1() throws Exception {
//        for (int i = 0; i < 800; i++) {
//            String k = i + "";
//            slabManager.put(k, Bean.instance((long) i, k.getBytes(), String.valueOf(i).getBytes()));
//        }
//    }
//
//    @Test
//    public void testStore2() throws Exception {
//        for (int i = 0; i < 400; i++) {
//            String k = i + "";
//            slabManager.put(k, Bean.instance((long) i, k.getBytes(), new byte[130]));
//        }
//    }
//
//    @Test
//    public void testStore3() throws Exception {
//        slabManager.put("a", Bean.instance(1L, "a".getBytes(), new byte[130]));
//        IndexManager.IdxValue idxValue = IndexManager.getIdx("a");
//        System.out.println(idxValue.slot + " " + idxValue.offset);
//        slabManager.put("a", Bean.instance(1L, "a".getBytes(), new byte[330]));
//        idxValue = IndexManager.getIdx("a");
//        System.out.println(idxValue.slot + " " + idxValue.offset);
//    }
//
//    @Test
//    public void testStore4() throws Exception {
//        slabManager.put("a", Bean.instance(1L, "a".getBytes(), new byte[130]));
//        IndexManager.IdxValue idxValue = IndexManager.getIdx("a");
//        Assert.assertTrue(idxValue.slot == 1 && idxValue.offset == 0);
//
//
//        slabManager.put("b", Bean.instance(1L, "b".getBytes(), new byte[130]));
//        idxValue = IndexManager.getIdx("b");
//        Assert.assertTrue(idxValue.slot == 1 && idxValue.offset == 1);
//
//
//        slabManager.put("c", Bean.instance(1L, "c".getBytes(), new byte[130]));
//        idxValue = IndexManager.getIdx("c");
//        Assert.assertTrue(idxValue.slot == 1 && idxValue.offset == 2);
//
//        slabManager.put("d", Bean.instance(1L, "d".getBytes(), new byte[130]));
//        idxValue = IndexManager.getIdx("d");
//        Assert.assertTrue(idxValue.slot == 1 && idxValue.offset == 3);
//
//        slabManager.remove("b");
//
//        slabManager.put("e", Bean.instance(1L, "e".getBytes(), new byte[130]));
//        idxValue = IndexManager.getIdx("e");
//        Assert.assertTrue(idxValue.slot == 1 && idxValue.offset == 1);
//    }
//
//    @Test
//    public void testSetAndGet() throws Exception {
//        for (int i = 0; i < 800; i++) {
//            String k = i + "";
//            slabManager.put(k, Bean.instance((long) i, k.getBytes(), String.valueOf(i).getBytes()));
//        }
//
//        for (int i = 0; i < 800; i++) {
//            Bean bean = slabManager.get(i + "");
//            Assert.assertEquals(bean.time, (long) i);
//            Assert.assertEquals(bean.value, String.valueOf(i).getBytes());
//        }
//    }
//
//    @Test
//    public void testDelete() throws Exception {
//        String k = "1";
//        slabManager.put(k, Bean.instance((long) 1, k.getBytes(), String.valueOf(1).getBytes()));
//        Bean bean = slabManager.get(k);
//        Assert.assertEquals(bean.time, 1L);
//        Assert.assertEquals(bean.value[0], 49);
//        slabManager.remove(k);
//        bean = slabManager.get(k);
//        Assert.assertNull(bean);
//    }
//
//    @Test
//    public void testAppend() throws Exception {
//        final String k = "1";
//        slabManager.put(k, Bean.instance((long) 1, k.getBytes(), String.valueOf(2).getBytes()));
//        slabManager.append(k, Bean.instance((long) 1, k.getBytes(), String.valueOf(3).getBytes()));
//
//        Bean bean = slabManager.get("1");
//        Assert.assertEquals(bean.time, 2L);
//        Assert.assertEquals(bean.value[0], 101);
//    }
//
//    static BeanMerge beanMerge = new BeanMerge() {
//        @Override
//        public Bean merge(Bean current, Bean old) {
//            // 思考merge策略，如果merge后size过大怎么清理
//            long time = current.time + old.time;
//            int newValue = (int) old.value[0] + (int) current.value[0];
//            byte[] na = new byte[1];
//            na[0] = (byte) newValue;
//            try {
//                return Bean.instance(time, old.getKey(), na);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//    };
//}
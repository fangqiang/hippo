//package pers.fq.hippo.store.core;
//
//import org.testng.Assert;
//import org.testng.annotations.Test;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/8/4
// */
//public class CleanExpireTest extends ConfigTest{
//
//    @Test
//    public void testCleanExpire() throws Exception {
//        for (int i = 0; i < 200; i++) {
//            Thread.sleep(100);
//            store.put(i+"", Bean.instance("va".getBytes()));
//        }
//    }
//}
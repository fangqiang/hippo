//package pers.fq.hippo.store.core;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import pers.fq.hippo.common.monitor.Monitor;
//import pers.fq.hippo.store.exp.OutOfMemory;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/8/4
// */
//public class StoreImpl implements Store{
//
//    private static final Logger logger = LoggerFactory.getLogger(StoreImpl.class);
//
//    //  检查long 类型溢出的问题 ???
//
//    private SlabManager slabManager;
//
//    public StoreImpl(BeanLifecycle beanMerge){
//        slabManager = new SlabManager(beanMerge);
//    }
//
//    @Override
//    public Bean get(String key) { //  get前先回调，清理数据 ???
//        long start = System.nanoTime();
//        Bean bean = slabManager.get(key);
//        Monitor.rt("get_rt", System.nanoTime() - start);
//        return bean;
//    }
//
//    @Override
//    public IdxValue put(String key, Bean bean) { //  吃掉这个异常 hrows OutOfMemory ???
//        try {
//            long start = System.nanoTime();
//            IdxValue value = slabManager.put(key, bean);
//            Monitor.rt("put_rt", System.nanoTime() - start);
//            return value;
//        }catch (Exception e){
//            logger.error("", e);
//            return null;
//        }
//    }
//
//    @Override
//    public void remove(String key) {
//        long start = System.nanoTime();
//        slabManager.remove(key);
//        Monitor.rt("remove_rt", System.nanoTime() - start);
//    }
//
//    @Override
//    public void append(String key, Bean currentBean) throws OutOfMemory {
//        long start = System.nanoTime();
//        slabManager.append(key, currentBean);
//        Monitor.rt("append_rt", System.nanoTime() - start);
//    }
//}
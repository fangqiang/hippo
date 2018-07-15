//package pers.fq.hippo.store.core;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import pers.fq.hippo.common.Constant;
//import pers.fq.hippo.common.Utils;
//import pers.fq.hippo.common.monitor.Monitor;
//import pers.fq.hippo.store.exp.OutOfMemory;
//import pers.fq.hippo.store.tag.Nullable;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/17
// */
//public class SlabManager {
//
//    private static final Logger logger = LoggerFactory.getLogger(SlabManager.class);
//
//    final SlabChooser slabChooser;
//
//    final BeanLifecycle beanLifecycle;
//
//    public SlabManager(BeanLifecycle beanLifecycle) {
//        this.beanLifecycle = beanLifecycle;
//
//        slabChooser = new SlabChooser();
//
//        // 定期清理过期的key
//        cleanSlabsPeriod();
//    }
//
//    /**
//     *  测试写堆外内存时（copyMemory方法）是否是线程安全的，如果是的话，那么读就不需要加锁 ???
//     */
//    public @Nullable Bean get(String key) {
//
//        synchronized (getLock(key)) {
//            IdxValue idxValue = getIdxValue(key);
//
//            if (idxValue == null) {
//                return null;
//            } else {
//                Bean bean = getBean(idxValue);
//
//                Monitor.avg("get_size", bean.value.length, "slab", getSlab(idxValue).chunkSize+"");
//
//                return bean;
//            }
//        }
//    }
//
//    /**
//     * @param key : 必须包含时间
//     * @param bean
//     * @return
//     * @throws OutOfMemory
//     */
//    public IdxValue put(final String key, Bean bean) throws OutOfMemory {
//
//        synchronized (getLock(key)) {
//            // 注意：先写入，再更新索引。因为依赖offset，所以jvm不会指令重排序（现在使用synchronized，暂时不用考虑）
//            // 注意：避免jvm指令重排序的影响。（现在使用synchronized，暂时不用考虑）
//            // 注意：必须保证：先删除索引，再删数据（现在使用synchronized，暂时不用考虑）
//
//            IdxValue newIdxValue = putValue(bean);
//
//            Monitor.avg("put_size", bean.value.length, "slab", getSlab(newIdxValue).chunkSize+"");
//
//            IdxValue oldIdxValue = getIdxValue(key);
//
//            if (oldIdxValue == null) {
//                // lru队列加入新key
//                getLruManager(newIdxValue).put(key, newIdxValue.lastUpdateTime);
//
//                // 之前不存在，插入索引
//                return putIdx(key, newIdxValue);
//            } else {
//                // lru队列加入新key
//                getLruManager(oldIdxValue).remove(key);
//                getLruManager(newIdxValue).put(key, newIdxValue.lastUpdateTime);
//
//                // 删除旧数据
//                deleteValue(oldIdxValue);
//
//                // 更新索引指向新的位置
//                oldIdxValue.update(newIdxValue.slot, newIdxValue.offset);
//                return newIdxValue;
//            }
//        }
//    }
//
//    /**
//     * 如果内存不足，则删掉10个最旧的数据。 如果内存依然不足，抛出异常。
//     *
//     * @param bean
//     * @return
//     * @throws OutOfMemory
//     */
//    private IdxValue putValue(Bean bean) throws OutOfMemoryError {
//        Slab slab = getSlabSize(bean);
//        try {
//            int offset = slab.putSomewhere(bean);
//            return IdxValue.newIdx(slab.id, offset);
//        }catch (OutOfMemoryError e){
//            // 强行删掉10个最旧的key
//            List<Map.Entry<String,Long>> key = slab.lruManager.getOldestKeys(10);
//            for (Map.Entry<String,Long> s : key) {
//                remove(s.getKey());
//            }
//
//            Monitor.tps("force_delete", key.size(), "slab", slab.chunkSize+"");
//
//            //  这个递归是否会有问题？??
//            return putValue(bean);
////            int offset = slab.putSomewhere(bean);
////            return IdxValue.newIdx(slab.id, offset);
//        }
//    }
//
//    public void remove(String key) {
//        synchronized (getLock(key)) {
//
//            @Nullable
//            IdxValue idxValue = getIdxValue(key);
//
//            if (idxValue == null) {
//                return;
//            } else {
//                // lru集合中删除
//                getLruManager(idxValue).remove(key);
//                // 删除索引
//                deleteIdx(key);
//                // 删除数据
//                deleteValue(idxValue);
//            }
//        }
//    }
//
//    public void removeIfNoBigThan(String key, long time) {
//        synchronized (getLock(key)) {
//            @Nullable
//            IdxValue idxValue = getIdxValue(key);
//            if(idxValue != null && time >= idxValue.lastUpdateTime){
//                remove(key);
//            }
//        }
//    }
//
//
//    /**
//     * @param key
//     * @param currentBean
//     */
//    public void append(String key, Bean currentBean) {
//        synchronized (getLock(key)) {
//            Bean mergeBean;
//
//            Bean bean = get(key);
//
//            if(beanLifecycle != null) {
//                if (bean == null) {
//                    // key不存在，或者已经被其他线程删除
//                    mergeBean = beanLifecycle.beforePut(key, currentBean, null);
//                } else {
//                    mergeBean = beanLifecycle.beforePut(key, currentBean, bean);
//                }
//            }else{
//                mergeBean = bean;
//            }
//
//            if (mergeBean == null) {
//                remove(key);
//            } else {
//                try {
//                    put(key, mergeBean);
//                } catch (OutOfMemory e) {
//                    logger.error("", e);
//                }
//            }
//        }
//    }
//
//    private final ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(
//            2,
//            new Utils.TFactory("slab_expire-"),
//            new ThreadPoolExecutor.DiscardPolicy());
//
//    /**
//     * 定期清理每个slab中过期的key
//     */
//    private void cleanSlabsPeriod(){
//        scheduled.scheduleAtFixedRate(
//                this::cleanExpire,
//                Config.EXPIRE_CLEAN_PERIOD_MS,
//                Config.EXPIRE_CLEAN_PERIOD_MS,
//                TimeUnit.MILLISECONDS
//        );
//    }
//
//    private void cleanExpire(){
//        try {
//            logger.info("start clean expire");
//            for (Slab slab : slabChooser.slabs) {
//                List<Map.Entry<String,Long>> keyList = slab.lruManager.getOldestKeys(100);
//                for (Map.Entry<String,Long> entry : keyList) {
//                    String key = entry.getKey();
//                    long time = entry.getValue();
//                    if(System.currentTimeMillis() - time > Config.TTL) {
//
//                        removeIfNoBigThan(key, time);
//
//                        Monitor.tps("expire_size", 1, "slab", slab.chunkSize + "");
//                    }
//                }
//            }
//        }catch (Exception e){
//            logger.error(Constant.CHECK_ERROR, e);
//        }
//    }
//
//    private void deleteIdx(String key) {
//        IndexManager.remove(key);
//    }
//
//    private void deleteValue(IdxValue idxValue) {
//        getSlab(idxValue).remove(idxValue.offset);
//    }
//
//    private IdxValue putIdx(String key, IdxValue idxValue) {
//        IndexManager.put(key, idxValue);
//        return idxValue;
//    }
//
//    private IdxValue putIdx(String key, int slab, int offset) {
//        IdxValue newIdxValue = IdxValue.newIdx(slab, offset);
//        IndexManager.put(key, newIdxValue);
//        return newIdxValue;
//    }
//
//    private IdxValue getIdxValue(String key){
//        return IndexManager.getIdx(key);
//    }
//
//    private Slab getSlab(IdxValue idxValue){
//        return slabChooser.getSlabByIdx(idxValue.slot);
//    }
//
//    private Slab getSlabSize(Bean bean){
//        return slabChooser.getSlabBySize(bean.size());
//    }
//
//    private Bean getBean(IdxValue idxValue){
//        return slabChooser.getSlabByIdx(idxValue.slot).get(idxValue.offset);
//    }
//
//    private Object getLock(final String key){
//        return LockPool.getLock(key);
//    }
//
//    private LruManager getLruManager(IdxValue idxValue){
//        return getSlab(idxValue).lruManager;
//    }
//}

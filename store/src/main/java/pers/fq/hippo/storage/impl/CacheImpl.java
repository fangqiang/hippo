package pers.fq.hippo.storage.impl;

import com.google.common.cache.RemovalNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fq.hippo.common.Utils;
import pers.fq.hippo.storage.*;
import pers.fq.hippo.storage.bean.BeanDO;
import pers.fq.hippo.store.tag.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/18
 */
public class CacheImpl implements Cache{

    private static final Logger logger = LoggerFactory.getLogger(CacheImpl.class);

    protected final Index index;

    private SlabManager slabManager;

    public CacheImpl(int maxSize, int ttlMs) {
        index = new IndexImpl(maxSize, ttlMs, this::expire);
        slabManager = SlabManager.instance();
    }

    public Index getIndex(){
        return index;
    }

    /**
     * 放入一个key，如果存储空间不够，则先删掉10个较旧的key，然后尝试再次写入。如果依然失败则放弃
     *
     * @param key
     * @param value
     */
    @Override
    public void put(String key, byte[] value) {

        boolean success = true;
        BeanDO beanDO;
        Slab slab;

        synchronized (LockPool.getLock(key)) {
            int oldSlabOffset = index.get(key);
            if(oldSlabOffset != -1){
                // 之前存在，先删除
                remove(key);
            }

            beanDO = new BeanDO(key, value, System.currentTimeMillis());

            slab = slabManager.getSlabBySize(beanDO.size());

            success = putBean(beanDO, slab);
        }

        if( ! success){
            overwrite(beanDO, slab);
        }
    }

    private boolean putBean(BeanDO beanDO, Slab slab){

        int offset = slab.put(beanDO);

        if(offset != -1){
            index.put(beanDO.key, SlabOffsetUtil.combine(slab.id, offset));
            return true;
        }else {
            return false;
        }
    }

    private void overwrite(BeanDO beanDO, Slab slab) {

        // 先删除10个最旧的key
        for (int i = 0; i < 10; i++) {
            BeanDO oldestBean = slab.getOldest();
            remove(oldestBean.key);
        }

        // 再次尝试写入，写入失败则放弃
        boolean success = putBean(beanDO, slab);

        if( ! success){
            logger.warn("write `{}` failed after retry", beanDO.key);
        }
    }

    @Override
    public boolean remove(String key) {
        synchronized (LockPool.getLock(key)) {
            int slabOffset = index.get(key);
            if (slabOffset != -1) {
                removeFromSlab(slabOffset);
                index.remove(key);
                return true;
            } else {
                return false;
            }
        }
    }

    @Nullable
    @Override
    public byte[] get(String key) {
        synchronized (LockPool.getLock(key)) {
            int slabOffset = index.get(key);
            if (slabOffset != -1) {
                BeanDO beanDO = getBean(slabOffset);
                return beanDO.value;
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean append(String key, byte[] value, BiFunction<byte[], byte[], byte[]> merge) {
        synchronized (LockPool.getLock(key)) {
            byte[] oldValue = get(key);
            byte[] result = merge.apply(oldValue, value);
            if (result != null) {
                put(key, result);
            }else{
                // 合并后值为null，则删除key
                if(oldValue != null) {
                    remove(key);
                }
            }
        }

        return true;
    }

    /**
     * key缓存后期后 回调函数
     */
    private void expire(RemovalNotification<String,Integer> notification){
        String key = notification.getKey();
        remove(key);
    }

    private void removeFromSlab(int slabOffset){
        int slabId = SlabOffsetUtil.getSlab(slabOffset);
        int offset = SlabOffsetUtil.getOffset(slabOffset);
        Slab slab = slabManager.getSlab(slabId);
        slab.remove(offset);
    }

    private BeanDO getBean(int slabOffset){
        int slabId = SlabOffsetUtil.getSlab(slabOffset);
        int offset = SlabOffsetUtil.getOffset(slabOffset);
        Slab slab = slabManager.getSlab(slabId);
        return slab.get(offset);
    }
}

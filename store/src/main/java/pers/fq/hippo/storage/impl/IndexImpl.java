package pers.fq.hippo.storage.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import pers.fq.hippo.storage.Index;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/18
 */
public class IndexImpl implements Index {

    /**
     * key实例, value实例一旦创建就不可更改，直到key被删除、value被gc回收
     */
    private final Cache<String, Integer> map;

    public IndexImpl(int maxSize, int ttlMs, RemovalListener<String, Integer> listener){
        map = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .concurrencyLevel(32)
                .expireAfterWrite(ttlMs, TimeUnit.MILLISECONDS)
                .removalListener(listener)
                .build();
    }

    @Override
    public void put(String key, int index) {
        map.put(key, index);
    }

    @Override
    public void remove(String key) {
        map.invalidate(key);
    }

    @Override
    public int get(String key) {
        Integer index = map.getIfPresent(key);
        return index != null ? index : -1;
    }

    @Override
    public ConcurrentMap<String, Integer> getMap(){
        return map.asMap();
    }
}

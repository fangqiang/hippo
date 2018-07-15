package pers.fq.hippo.biz;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import pers.fq.hippo.store.tag.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * @Description: 环境数据，避免多次从堆外复制到堆内。因为同一个维度短时间内常常会需要计算多个指标
 * @author: fang
 * @date: Created by on 18/12/21
 */
public class DataCache {
    private final Cache<String, byte[]> map;

    public DataCache() {
        map = CacheBuilder.newBuilder()
                .maximumSize(8192)
                .concurrencyLevel(32)
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .build();
    }

    public void put(String key, byte[] value){
        map.put(key, value);
    }

    @Nullable
    public byte[] get(String key){
        return map.getIfPresent(key);
    }
}

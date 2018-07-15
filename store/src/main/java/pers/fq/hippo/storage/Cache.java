package pers.fq.hippo.storage;

import pers.fq.hippo.store.tag.Nullable;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 缓存入口层
 *
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/18
 */
public interface Cache {

    void put(String key, byte[] value);

    boolean remove(String key);

    @Nullable
    byte[] get(String key);

    /**
     * @param key
     * @param value
     * @param merge: 合并2个结果集， 第一个参数代表旧数据，第二个参数代表当前数据。如果返回结果为null，则删除key
     * @return
     */
    boolean append(String key, byte[] value, BiFunction<byte[], byte[], byte[]> merge);
}

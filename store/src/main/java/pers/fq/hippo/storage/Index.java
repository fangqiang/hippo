package pers.fq.hippo.storage;

import java.util.concurrent.ConcurrentMap;

/**
 * 数据的索引
 *
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/18
 */
public interface Index {
    void put(String index, int addressDO);

    void remove(String index);

    /**
     * 如果索引不存在，返回-1
     *
     * @param index
     * @return
     */
    int get(String index);

    ConcurrentMap<String, Integer> getMap();
}

package pers.fq.hippo.storage;

import pers.fq.hippo.storage.bean.BeanDO;

/**
 * slab的结构
 *
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/18
 */
public abstract class Slab {

    public final int id;
    public final int chunkSize;

    public Slab(int id, int chunkSize) {
        this.id = id;
        this.chunkSize = chunkSize;
    }

    public abstract int put(BeanDO beanDO);

    public abstract void remove(int offset);

    public abstract BeanDO get(int offset);

    public abstract int getFreeChunk();

    public abstract BeanDO getOldest();

    public long absoluteAddress(int offset) {
        return (long) offset * chunkSize;
    }
}

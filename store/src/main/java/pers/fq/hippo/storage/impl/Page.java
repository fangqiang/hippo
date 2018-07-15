package pers.fq.hippo.storage.impl;

import pers.fq.hippo.storage.bean.BeanDO;

import java.nio.ByteBuffer;

/**
 * 1、绝不可能并发写一个offset
 * 2、不同的offset可以并发写
 *
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/19
 */
public class Page {

    public final ByteBuffer buffer;

    public Page(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public BeanDO read(int pos) {
        ByteBuffer dup = buffer.duplicate();
        dup.position(pos);
        return BeanDO.parseBean(dup);
    }

    public void write(int pos, BeanDO beanDO) {
        ByteBuffer dup = buffer.duplicate();
        dup.position(pos);
        dup.put(beanDO.toWritableByteBuffer());
    }
}
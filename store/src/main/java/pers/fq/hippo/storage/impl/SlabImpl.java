package pers.fq.hippo.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fq.hippo.common.monitor.Monitor;
import pers.fq.hippo.storage.Slab;
import pers.fq.hippo.storage.bean.BeanDO;
import pers.fq.hippo.store.tag.Nullable;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/18
 */
public class SlabImpl extends Slab {

    private static final Logger logger = LoggerFactory.getLogger(SlabImpl.class);

    /**
     * 存储数据的地方
     */
    final AtomicReferenceArray<Page> pages = new AtomicReferenceArray((int) (Config.MAX_OFF_HEAP_SIZE / Config.PAGE_SIZE));

    final AtomicInteger pageSize = new AtomicInteger(0);

    /**
     * 空闲chunk链表，并按值排序
     */
    final ConcurrentSkipListSet<Integer> freeChunks = new ConcurrentSkipListSet();

    final AtomicInteger expireIdx = new AtomicInteger(0);

    public SlabImpl(int id, int chunkSize) {
        super(id, chunkSize);

        registMonitor();
    }

    /**
     * @param beanDO
     * @return slab内的offset, 如果没有可用空间返回-1
     */
    @Override
    public int put(BeanDO beanDO) {
        Monitor.tps("put_tps", 1, "slab", chunkSize+"");
        Monitor.avg("get_size", beanDO.value.length, "slab", chunkSize+"");

        int offset = getFreeChunk();
        if(offset == -1){
            return -1;
        }

        long absAddress = absoluteAddress(offset);

        long pageIdx = absAddress / Config.PAGE_SIZE;
        long offsetInPage = absAddress % Config.PAGE_SIZE;

        Page page = pages.get((int) pageIdx);

        page.write((int) offsetInPage, beanDO);

        return offset;
    }

    @Override
    public void remove(int offset) {
        Monitor.tps("remove_tps", 1, "slab", chunkSize+"");

        freeChunks.add(offset);
    }

    @Override
    public BeanDO get(int offset) {
        Monitor.tps("get_tps", 1, "slab", chunkSize+"");

        long absAddress = absoluteAddress(offset);

        long pageIdx = absAddress / Config.PAGE_SIZE;
        long offsetInPage = absAddress % Config.PAGE_SIZE;

        Page page = pages.get((int) pageIdx);

        return page.read((int) offsetInPage);
    }

    @Override
    public int getFreeChunk() {
        Integer offset = freeChunks.pollFirst();

        if(offset == null){
            if( ! Allocater.isFull) {
                // 扩容一个page （有可能扩容失败）
                extendPage();
                // 再次尝试获取一个空页面
                offset = freeChunks.pollFirst();
            }
        }

        return offset != null? offset : -1;
    }

    @Override
    public BeanDO getOldest() {
        int offset = expireIdx.getAndIncrement();
        if(offset >= capacity()/chunkSize - 2){
            expireIdx.set(0);
        }
        return get(offset);
    }

    private synchronized void extendPage() {
        @Nullable
        Page page = Allocater.allocatePage();
        if (page != null) {
            pages.set(pageSize.getAndIncrement(), page);

            long size = Config.PAGE_SIZE / chunkSize;
            long base = size * (pageSize.get() - 1);

            for (long i = 0; i < size; i++) {
                freeChunks.add((int) (base + i));
            }
        }
    }

    private long capacity() {
        return pageSize.get() * Config.PAGE_SIZE;
    }

    public void registMonitor(){
        Monitor.regist(() -> {
            Monitor.value("mem_used", (capacity() - freeChunks.size()*chunkSize)/1024/1024, "slab", chunkSize+"");
            Monitor.value("mem_allocation", capacity() / 1024 / 1024 , "slab", chunkSize+"");
            Monitor.value("free_chunks", freeChunks.size(), "slab", chunkSize+"");
            Monitor.value("key_size", capacity()/chunkSize - freeChunks.size(), "slab", chunkSize+"");
        });
    }
}

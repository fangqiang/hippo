//package pers.fq.hippo.store.core;
//
//import pers.fq.hippo.common.monitor.Monitor;
//import pers.fq.hippo.store.exp.HippoException;
//import pers.fq.hippo.store.exp.OutOfMemory;
//import pers.fq.hippo.store.tag.ThreadSafe;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ConcurrentSkipListSet;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * 不同的key并发进来，可以分别取到各自的offset。
// * <p>
// * freeChunks：concurrent变量，保证了并发
// * seqIndex：concurrent变量，保证了并发
// * pages：synchronized关键字，保证了并发
// *
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/17
// */
//@ThreadSafe
//public class Slab {
//
//
//
//    /**
//     * slab的唯一标识
//     */
//    public final int id;
//
//    /**
//     * 每个chunk的大小，必须为long类型，否则计算地址时可能出错
//     */
//    final long chunkSize;
//
//    /**
//     * 存储数据的地方
//     */
//    final List<Page> pages = new ArrayList<Page>();
//
//    /**
//     * 空闲chunk链表，并按值排序
//     */
//    final ConcurrentSkipListSet<Integer> freeChunks = new ConcurrentSkipListSet();
//
//    /**
//     * 指向下一个可用空间
//     */
//    final AtomicInteger seqIndex = new AtomicInteger(0);
//
//    /**
//     * 维护一个LRU队列，淘汰时使用
//     */
//    final LruManager lruManager = new LruManager();
//
//    public Slab(int id, int chunkSize) {
//        this.id = id;
//        this.chunkSize = chunkSize;
//
//        registMonitor();
//    }
//
//    /**
//     * 删除一个chunk
//     */
//    public boolean remove(int offset) {
//        Monitor.tps("remove_tps", 1, "slab", chunkSize+"");
//
//        return freeChunks.add(offset);
//    }
//
//    /**
//     * 从一个chunk中读取一个Bean
//     */
//    public Bean get(int offset) {
//        Monitor.tps("get_tps", 1, "slab", chunkSize+"");
//
//        long absAddress = absoluteAddress(offset);
//
//        if (absAddress > capacity()) {
//            throw HippoException.INDEX_OUT_OF_BOUND;
//        }
//
//        long pageIdx = absAddress / Config.PAGE_SIZE;
//        long offsetInPage = absAddress % Config.PAGE_SIZE;
//
//        Page page = pages.get((int) pageIdx);
//
//        return page.read((int) offsetInPage);
//    }
//
//    /**
//     * 写入一个Bean，并返回Bean的地址
//     */
//    public int putSomewhere(Bean bean) throws OutOfMemoryError {
//        Monitor.tps("put_tps", 1, "slab", chunkSize+"");
//
//        // 优先放入空闲chunk
//        int offset = putIntoFreeChunk(bean);
//
//        // 成功放入空闲chunk
//        if (offset != -1) {
//            return offset;
//        } else {
//            // 保护指针资源，可能cleaner正在回收page
//            synchronized (seqIndex) {
//                int seqOffset = seqIndex.getAndIncrement();
//
//                long absAddress = absoluteAddress(seqOffset);
//
//                ensure(absAddress);
//
//                save(absAddress, bean);
//
//                return seqOffset;
//            }
//        }
//    }
//
//    /**
//     * 放入空闲列表，如果空闲列表没有空间，则放弃
//     */
//    private int putIntoFreeChunk(Bean bean) {
//        Integer offset = freeChunks.pollFirst();
//        if (offset != null) {
//            save(absoluteAddress(offset), bean);
//            return offset;
//        } else {
//            return -1;
//        }
//    }
//
//    /**
//     * 将数据保存到一个绝对地址
//     */
//    private void save(long absAddress, Bean bean) {
//        long pageIdx = absAddress / Config.PAGE_SIZE;
//        long offsetInPage = absAddress % Config.PAGE_SIZE;
//
//        Page page = pages.get((int) pageIdx);
//        page.write((int) offsetInPage, bean);
//    }
//
//    @ThreadSafe
//    private synchronized void ensure(long capacity) throws OutOfMemoryError {
//        // 并发操作，其他线程可能已经申请了内存
//        if (capacity < capacity()) {
//            return;
//        }
//
//        while (true) {
//
//            pages.add(PageManager.allocatePage());
//
//            Monitor.tps(Monitor.PAGE_ALLCATION, 1, "slab", chunkSize+""); // 监控
//
//            if (capacity < capacity()) {
//                return;
//            }
//        }
//    }
//
//    private long absoluteAddress(int offset) {
//        return offset * chunkSize;
//    }
//
//    private long capacity() {
//        return pages.size() * Config.PAGE_SIZE;
//    }
//
//    public void registMonitor(){
//        Monitor.regist(() -> {
//            Monitor.value("mem_used", (seqIndex.get() - freeChunks.size()) * chunkSize, "slab", chunkSize+"");
//            Monitor.value("page_num", pages.size(), "slab", chunkSize+"");
//            Monitor.value("free_chunks", freeChunks.size(), "slab", chunkSize+"");
//            Monitor.value("seq_index", seqIndex.get(), "slab", chunkSize+"");
//            Monitor.value("key_size", lruManager.set.size(), "slab", chunkSize+"");
//            Monitor.value("key_size1", IndexManager.map.size(), "slab", chunkSize+"");
//        });
//    }
//}

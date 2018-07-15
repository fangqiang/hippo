//package pers.fq.hippo.store.core;
//
//import pers.fq.hippo.store.exp.HippoException;
//import pers.fq.hippo.store.exp.OutOfMemory;
//import pers.fq.hippo.store.tag.ThreadSafe;
//
//import java.nio.ByteBuffer;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/18
// */
//@ThreadSafe
//public class PageManager {
//
//    public static ConcurrentLinkedQueue<Page> freePages = new ConcurrentLinkedQueue();
//
//    public static long allocatedMemory = 0L;
//
//    public static final OutOfMemoryError OOM = new OutOfMemoryError();
//
//    public synchronized static Page allocatePage() throws OutOfMemoryError {
//        Page page = freePages.poll();
//
//        if (page != null) {
//            return page;
//        }
//
//        if (allocatedMemory < Config.MAX_OFF_HEAP_SIZE) {
//            // 向操作系统申请
//            ByteBuffer buffer = ByteBuffer.allocateDirect((int)Config.PAGE_SIZE);
//            allocatedMemory += Config.PAGE_SIZE;
//
//            return new Page(buffer);
//        } else {
//            throw OOM;
//        }
//    }
//
//    public static void add(Page page) {
//        freePages.offer(page);
//    }
//}

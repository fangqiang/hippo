//package pers.fq.hippo.store.core;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/17
// */
//public class SlabCleaner extends Thread {
//    private static final Logger logger = LoggerFactory.getLogger(SlabCleaner.class);
//
//    SlabManager slabManager;
//
//    public SlabCleaner(SlabManager slabManager) {
//        super("SlabCleaner-Thread");
//        setDaemon(true);
//
//        this.slabManager = slabManager;
//
//        start();
//    }
//
//    @Override
//    public void run() {
//        for (; ; ) {
//            try {
//
//                // 定期清理
//                Thread.sleep(Config.FRAGMENT_CLEAN_PERIOD_MS);
//
//                logger.info("start clean fragment item");
//
//                cleanAllSlab();
//
//                logger.info("stop clean fragment item");
//
//            } catch (Exception e) {
//                logger.error("clean fragment failed, ", e);
//            }
//        }
//    }
//
//    public void cleanAllSlab() {
//        for (Slab slab : slabManager.slabChooser.slabs) {
//            long start = System.currentTimeMillis();
//
//            int cleanCnt = cleanFragment(slab);
//
//            long cost = System.currentTimeMillis() - start;
//            logger.info("start clean fragment, slab_size=[{}], clean [{}], cost time: [{}]", slab.chunkSize, cleanCnt, cost);
//        }
//    }
//
//    /**
//     * 注意： 这里是移动大块数据，消耗内存带宽很高。必须控制清理频率
//     */
//    private int cleanFragment(Slab slab) {
//        int cleanCnt = 0;
//
//        synchronized (slab.seqIndex) {
//
//            for (; ; ) {
//                if (!matchRule(slab)) {
//                    break;
//                }
//
//                int offset = slab.seqIndex.get() - 1;
//                Bean bean = slab.get(offset);
//                String key = new String(bean.key);
//
//                synchronized (LockPool.getLock(key)) {
//
//                    if (IndexManager.exist(key)) {
//                        // 注意： 这里是移动大块数据，消耗内存带宽很高。必须控制清理频率
//                        int newOffset = slab.putIntoFreeChunk(bean);
//
//                        if (newOffset == -1) {
//                            // 空闲列表没有空间了
//                            return cleanCnt;
//                        } else {
//                            if (newOffset < offset) {
//                                slab.seqIndex.decrementAndGet();
//                                cleanCnt++;
//                            } else {
//                                new RuntimeException("unspect error, move chunk, but new address is bigger than old");
//                            }
//                        }
//                    } else {
//                        // 先从空闲列表中回收
//                        slab.freeChunks.remove(offset);
//                        // 索引前移
//                        slab.seqIndex.decrementAndGet();
//                    }
//                }
//            }
//
//            // 必须在锁住seqIndex的时候操作
//            long pageCnt = slab.seqIndex.get() / (Config.PAGE_SIZE / slab.chunkSize) + 1;
//            while (slab.pages.size() > pageCnt + Config.SPARE_PAGE_SIZE) {
//                int lastIdx = slab.pages.size() - 1;
//                Page page = slab.pages.remove(lastIdx);
//                PageManager.add(page);
//            }
//        }
//
//        return cleanCnt;
//    }
//
//    /**
//     * 触发碎片回收的规则
//     * <p>
//     * 1、空闲空间大于10个碎片
//     */
//    private boolean matchRule(Slab slab) {
//        return slab.freeChunks.size() > Config.FRAGMENT_LIMIT_FOR_CLEARN;
//    }
//}

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
//public class ExpireCleaner extends Thread {
//
//    private static final Logger logger = LoggerFactory.getLogger(ExpireCleaner.class);
//
//
//    SlabManager slabManager;
//
//    public ExpireCleaner(SlabManager slabManager) {
//        super("ExpireCleaner-Thread");
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
//                // 定期清理
//                Thread.sleep(Config.EXPIRE_CLEAN_PERIOD_MS);
//
//                logger.info("start clean expire item");
//
//                cleanAllSlab();
//
//                logger.info("stop clean expire item");
//            } catch (Exception e) {
//                logger.error("clean expire failed, ", e);
//            }
//        }
//    }
//
//    public void cleanAllSlab() {
//        // 对每个slab清理
//        for (Slab slab : slabManager.slabChooser.slabs) {
//            long start = System.currentTimeMillis();
//            int deleteCnt = cleanSlab(slab);
//            long cost = System.currentTimeMillis() - start;
//            logger.info("start clean slab slab_size=[{}], clean [{}], cost time: [{}]", slab.chunkSize, deleteCnt, cost);
//        }
//    }
//
//    public int cleanSlab(Slab slab) {
//        long now = System.currentTimeMillis();
//
//        int deleteCnt = 0;
//
//        for (int i = 0; i < slab.seqIndex.get(); i++) {
//            // 扫描存储层的数据块
//            Bean.Meta beanMeta = slab.getBeanMeta(i);
//
//            if (IndexManager.exist(beanMeta.key)) {
//                // 只有确定超时了，才开始删除，否则可能增加锁的竞争
//                // 触发【尝试】删除，因为有可能这个时刻，数据被更新了
//                if (now - beanMeta.time > Config.TTL) {
//                    boolean res = slabManager.removeIfExpire(key);
//                    if (res) {
//                        deleteCnt++;
//                    } else {
//                        // 检查为什么会到这里 ？？？？？
//                        System.out.println("xx");
//                    }
//                }
//            } else {
//                // 索引不存在（代表这条数据不存在，或者已经被删了）
//            }
//        }
//        return deleteCnt;
//    }
//}

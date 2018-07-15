//package pers.fq.hippo.store.recover;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import pers.fq.hippo.store.core.Bean;
//import pers.fq.hippo.store.core.Config;
//import pers.fq.hippo.store.core.IndexManager;
//import pers.fq.hippo.store.core.SlabManager;
//
//import java.io.*;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.concurrent.LinkedBlockingQueue;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/25
// */
//public class PersistImageAsync {
//
//    // 写文件是异常退出，如何清理垃圾备份文件 ？？？
//    // 考虑每个步骤挂了，有什么影响 （所有步骤） ？？？
//
//    private static final Logger logger = LoggerFactory.getLogger(PersistImageAsync.class);
//
//    private final String dirName = Recover.LOG_DIR + new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date()) + File.separator;
//
//    private int fileIndex = 1;
//
//    /**
//     * 最多只有一个线程在持久化
//     */
//    static volatile boolean isRunning = false;
//
//    LinkedBlockingQueue<Bean> queue = new LinkedBlockingQueue(1000);
//
//    BufferedOutputStream outputStream;
//
//    SlabManager slabManager;
//
//    int count = 0;
//
//    public PersistImageAsync(SlabManager slabManager) {
//
//        this.slabManager = slabManager;
//
//        if (tryUpdate()) {
//
//            startPersist();
//
//            startCollectData();
//
//        } else {
//            throw new RuntimeException("persist thread is already running");
//        }
//    }
//
//    public synchronized boolean tryUpdate(){
//        if(isRunning){
//            return false;
//        }else{
//            isRunning = true;
//            return true;
//        }
//    }
//
//    public void add(Bean bean) {
//        for (; ; ) {
//            try {
//                queue.put(bean);
//                return;
//            } catch (InterruptedException e) {
//                ; // 继续循环
//            } catch (NullPointerException e1) {
//                return;
//            }
//        }
//    }
//
//    public void startCollectData() {
//        new Thread(() -> {
//
//            logger.info("start collect data");
//            // 遍历所有bean
//            IndexManager.map.forEachKey(1, k -> {
//                Bean bean = slabManager.get(k);
//                add(bean);
//            });
//
//            // 最后一个元素，标记处理完成
//            add(Bean.TerminateBean);
//
//            logger.info("collect data finished");
//
//        }, "persist-thread-collect-data").start();
//    }
//
//    public void startPersist() {
//        new Thread(() -> {
//            logger.info("start persist");
//            while (true) {
//                try {
//                    Bean bean = queue.take();
//
//                    if (bean.isTerminateBean()) {
//                        break;
//                    } else {
//                        persist(bean);
//                    }
//
//                } catch (Exception e) {
//                    logger.error("", e);
//                }
//            }
//            logger.info("persist finished, count: [{}]", count);
//
//            close();
//
//        }, "persist-thread-write").start();
//    }
//
//    private BufferedOutputStream nextStream() throws FileNotFoundException {
//        return new BufferedOutputStream(new FileOutputStream(getNextFileName()));
//    }
//
//    private void persist(Bean bean) throws IOException {
//        if (outputStream == null) {
//            outputStream = nextStream();
//        }
//
//        // 每100w个key一个文件
//        if (count++ % Config.KEY_SIZE_PER_FILE == 0) {
//            outputStream.flush();
//            outputStream.close();
//            outputStream = nextStream();
//        }
//
//        BeanRecoverUtil.writeBean(outputStream, bean);
//    }
//
//    private synchronized void close() {
//        try {
//            outputStream.flush();
//            outputStream.close();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            isRunning = false;
//        }
//    }
//
//    private String getNextFileName() {
//        File f = new File(dirName);
//        if (!f.exists()) {
//            f.mkdirs();
//        }
//        return dirName + (fileIndex++) + ".db";
//    }
//}

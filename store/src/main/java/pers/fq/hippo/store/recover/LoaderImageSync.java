//package pers.fq.hippo.store.recover;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import pers.fq.hippo.store.core.Bean;
//import pers.fq.hippo.store.core.SlabManager;
//import pers.fq.hippo.store.exp.OutOfMemory;
//import pers.fq.hippo.store.tag.Nullable;
//import pers.fq.hippo.store.util.CommonUtil;
//
//import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.stream.Collectors;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/25
// */
//public class LoaderImageSync {
//
//    private static final Logger logger = LoggerFactory.getLogger(LoaderImageSync.class);
//
//    /**
//     * 只能load一次
//     */
//    static volatile boolean isRunning = false;
//
//    LinkedBlockingQueue<Bean> queue = new LinkedBlockingQueue(1000);
//
//    BufferedInputStream inputStream;
//
//    SlabManager slabManager;
//
//    public LoaderImageSync(SlabManager slabManager) throws IOException {
//        this.slabManager = slabManager;
//
//        if (tryUpdate()) {
//
//            start();
//
//        } else {
//            throw new RuntimeException("loader thread is already running");
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
//    public void load() throws IOException {
//        List<String> files = getDBFiles();
//
//        for (String file : files) {
//            logger.info("load file: [{}]", file);
//
//            inputStream = new BufferedInputStream(new FileInputStream(file));
//
//            while (true) {
//
//                @Nullable
//                Bean bean = BeanRecoverUtil.readBean(inputStream);
//
//                // null代表读到文件末尾了
//                if (bean == null) {
//                    inputStream.close();
//                    break;
//                } else {
//                    add(bean);
//                }
//            }
//
//            logger.info("load finished: [{}]", file);
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
//    public void start() throws IOException {
//        new Thread(() -> { //  可以考虑多线程消费，多线程如何判断，消费完毕？？
//
//            int count = 0;
//
//            logger.info("start load");
//            while (true) {
//                try {
//                    Bean bean = queue.take();
//
//                    // 结束标记
//                    if (bean.isTerminateBean()) {
//                        break;
//                    } else {
//                        try {
//                            String key = new String(bean.getKey());
//                            //  可以改成append
//                            slabManager.put(key, bean);
//                        } catch (OutOfMemory e) {
//                            logger.error("fail when loading", e);
//                        }
//                        count++;
//                    }
//                } catch (Exception e) {
//                    logger.error("", e);
//                }
//            }
//            logger.info("load finished, count: [{}]", count);
//        }, "loader-thread").start();
//
//        load();
//    }
//
//
//    private synchronized void close() {
//        try {
//            inputStream.close();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private String getLatestDir() {
//        // 获得指定文件对象
//        File dir = new File(Recover.LOG_DIR);
//        // 获得该文件夹内的所有文件
//        File[] array = dir.listFiles();
//
//        if (array.length > 0) {
//            List<String> names = Arrays.stream(array).map(File::getName).collect(Collectors.toList());
//            return CommonUtil.getMax(names);
//        } else {
//            throw new RuntimeException("no dir to recover");
//        }
//    }
//
//    private List<String> getDBFiles() {
//        String latestDir = getLatestDir();
//        // 获得指定文件对象
//        File dir = new File(Recover.LOG_DIR + latestDir);
//        // 获得该文件夹内的所有文件
//        File[] array = dir.listFiles();
//
//        if (array.length > 0) {
//            return Arrays.stream(array).map(File::getAbsolutePath).collect(Collectors.toList());
//        } else {
//            throw new RuntimeException("no files to recover");
//        }
//    }
//}

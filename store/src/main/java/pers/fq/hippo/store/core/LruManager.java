//package pers.fq.hippo.store.core;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.*;
//import java.util.concurrent.*;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/8/4
// */
//public class LruManager {
//
//    private static final Logger logger = LoggerFactory.getLogger(LruManager.class);
//
//    LinkedBlockingQueue<Task> queue = new LinkedBlockingQueue<Task>(8192);
//
//    final LinkedHashMap<String,Long> set = new LinkedHashMap(16, 0.75f, true);
//
//    private Object lock = new Object();
//
//    public LruManager(){
//        start();
//    }
//
//    public void put(String key, long value){
//        for(;;) {
//            try {
//                queue.put(Task.newPutTask(key, value));
//                return;
//            } catch (InterruptedException e) { }
//        }
//    }
//
//    public void remove(String key){
//        for(;;) {
//            try {
//                queue.put(Task.newRemoveTask(key));
//                return;
//            } catch (InterruptedException e) { }
//        }
//    }
//
//    public void start(){
//        new Thread(()->{
//            while(true){
//                try {
//                    Task task = queue.take();
//                    if (task.isPutTask()) {
//                        synchronized (lock) {
//                            set.put(task.key, task.value);
//                        }
//                    }else if (task.isRemoveTask()) {
//                        synchronized (lock) {
//                            set.remove(task.key);
//                        }
//                    }else{
//                        ;
//                    }
//                } catch (InterruptedException e) {
//                    logger.error("", e);
//                }
//            }
//        },"LruManager-Process").start();
//    }
//
//    public synchronized List<Map.Entry<String,Long>> getOldestKeys(int maxSize){
//        int cnt = maxSize;
//
//        // 避免ConcurrentModificationException 异常
//        synchronized (lock) {
//
//            List<Map.Entry<String,Long>> list = new ArrayList<>(maxSize);
//
//            for (Map.Entry<String, Long> entry : set.entrySet()) {
//                list.add(entry);
//
//                cnt--;
//
//                if (cnt == 0) {
//                    break;
//                }
//            }
//
//            return list;
//        }
//    }
//
//    static class Task{
//        static int PUT = 1;
//        static int REMOVE = 2;
//
//
//        int code;
//        String key;
//        long value;
//
//        public Task(int code, String key, long value) {
//            this.code = code;
//            this.key = key;
//            this.value = value;
//        }
//
//        public boolean isPutTask(){
//            return code == PUT;
//        }
//
//        public boolean isRemoveTask(){
//            return code == REMOVE;
//        }
//
//        public static Task newPutTask(String key, long value) {
//            return new Task(PUT, key, value);
//        }
//
//        public static Task newRemoveTask(String key) {
//            return new Task(REMOVE, key, 0L);
//        }
//    }
//}

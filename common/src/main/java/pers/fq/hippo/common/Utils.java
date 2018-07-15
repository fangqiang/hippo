package pers.fq.hippo.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/4
 */
public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static final HashMap SINGLTION_HASHMAP = new HashMap();

    public static class TFactory implements ThreadFactory {
        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        String name;

        public TFactory(String name){
            this.name = name;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = this.defaultFactory.newThread(r);
            if (!thread.isDaemon()) {
                thread.setDaemon(true);
            }

            thread.setName(name + this.threadNumber.getAndIncrement());
            return thread;
        }
    }

    public static void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static final HashMap HASH_MAP = new HashMap();


    public static final int STANDARD_THREAD_SIZE = 2 * Runtime.getRuntime().availableProcessors();

    public static void checkOrDie(boolean bool, String msg){
        if(!bool){
            logger.error(Constant.CHECK_ERROR +" : {}", msg);
            sleep(1000);
            System.exit(0);
        }
    }

    public static String getLocalIp(){
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}

package pers.fq.hippo.common.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fq.hippo.common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/4
 */
public class Monitor {

    public static boolean ENABLE_MONITOR = false;

    private static ScheduledThreadPoolExecutor scheduled;

    static List<Peroid> peroids;

    public static void enable(){
        ENABLE_MONITOR = true;

        scheduled = new ScheduledThreadPoolExecutor(
                2,
                new Utils.TFactory("monitor-"),
                new ThreadPoolExecutor.DiscardPolicy());

        peroids = new ArrayList<>();

        scheduled.scheduleAtFixedRate(() -> {
            for (Peroid peroid : peroids) {
                peroid.period();
            }
        }, 10_000, 10_000, TimeUnit.MILLISECONDS);
    }

    public static void value(String key, double plus){
        if(ENABLE_MONITOR) {
            // TODO
        }
    }

    public static void value(String key, double plus, String tag1, String tagVal1){
        if(ENABLE_MONITOR) {
            // TODO
        }
    }

    public static void tps(String key, double plus){
        if(ENABLE_MONITOR) {
            // TODO
        }
    }

    public static void tps(String key, double plus, String tag1, String tagVal1){
        if(ENABLE_MONITOR) {
            // TODO
        }
    }

    public static void tps(String key, double plus, String tag1, String tagVal1, String tag2, String tagVal2){

    }

    public static void rt(String key, double plus){
        if(ENABLE_MONITOR) {
            // TODO
        }
    }

    public static void rt(String key, double plus, String tag1, String tagVal1){

    }

    public static void rt(String key, double plus, String tag1, String tagVal1, String tag2, String tagVal2){

    }

    public static void avg(String key, double plus, String tag1, String tagVal1){
        if(ENABLE_MONITOR) {
            // TODO
        }
    }

    public interface Peroid{
        void period();
    }

    public synchronized static void regist(Peroid peroid){
        if(ENABLE_MONITOR) {
            peroids.add(peroid);
        }
    }
}

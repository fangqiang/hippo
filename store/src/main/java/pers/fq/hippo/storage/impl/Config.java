package pers.fq.hippo.storage.impl;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/18
 */
public class Config {

    public static int CHUNK_MIN_SIZE = 512;
    public static int CHUNK_MAX_SIZE = 1024 * 1024;

    public static int CACHE_MAX_SIZE = 500_0000;
    public static int CACHE_TTL = 86400_000 * 7;

    public static int LOCK_SIZE = 1024 * 16;

    /**
     * 每个页的大小
     * 每次向操作系统申请的内存大小
     * 每个页只少能放下一个chunk
     */
    public static long PAGE_SIZE = 1024 * 1024 * 3 * 5;

    public static long MAX_OFF_HEAP_SIZE = 1024L * 1024L * 1024L * 200L;
}

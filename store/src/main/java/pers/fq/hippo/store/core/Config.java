//package pers.fq.hippo.store.core;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/18
// */
//public class Config {
//
//    public static int MIN_CHUNK_SIZE = 512;
//    public static int MAX_CHUNK_SIZE = 1024 * 1024;
//
//    public static int EXPIRE_CLEAN_PERIOD_MS = 600_000;
//
//    public static long TTL = 86400_000 * 7;
//
//    public static int LOCK_SIZE = 1024 * 16;
//
//    /**
//     * 每个页的大小
//     * 每次向操作系统申请的内存大小
//     * 每个页只少能放下一个chunk
//     */
//    public static long PAGE_SIZE = 1024 * 1024 * 8;
//
//    /**
//     * 回收时，允许保留10个空余的page
//     */
//    public static int SPARE_PAGE_SIZE = 10;
//
//    public static long MAX_OFF_HEAP_SIZE = 1024L * 1024L * 1024L * 200L;
//
//    public static long KEY_SIZE_PER_FILE = 100_0000;
//}

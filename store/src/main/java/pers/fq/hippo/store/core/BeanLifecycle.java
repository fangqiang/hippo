//package pers.fq.hippo.store.core;
//
//import pers.fq.hippo.store.tag.Nullable;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/18
// */
//public interface BeanLifecycle {
//
//    /**
//     * 自定义合并策略，如果返回null，这个key会被删除
//     */
//    Bean beforeReturn(String key, @Nullable Bean bean);
//
//    /**
//     * 自定义合并策略，如果返回null
//     */
//    void afterReturn(String key, @Nullable Bean bean);
//
//    /**
//     * 自定义合并策略，如果返回null，这个key会被删除
//     */
//    Bean beforePut(String key, Bean current, @Nullable Bean old);
//
//    void afterPut(String key, Bean current, @Nullable Bean old);
//
//    void beforeRemove(String key, @Nullable Bean bean);
//
//    void afterRemove(String key, @Nullable Bean bean);
//}

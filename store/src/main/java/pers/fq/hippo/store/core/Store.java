//package pers.fq.hippo.store.core;
//
//import pers.fq.hippo.store.exp.OutOfMemory;
//import pers.fq.hippo.store.tag.Nullable;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/28
// */
//public interface Store {
//
//    /**
//     * 获取一个key的结果
//     */
//    @Nullable
//    public Bean get(String key);
//
//    /**
//     * 覆盖写入一个key
//     */
//    IdxValue put(final String key, Bean bean);
//
//    /**
//     * 删除一个key的数据
//     */
//    void remove(String key);
//
//    /**
//     * 追加一个数据，可以自定义和历史数据的合并策略
//     */
//    public void append(String key, Bean currentBean) throws OutOfMemory;
//}

//package pers.fq.hippo.store.exp;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/17
// */
//public class HippoException {
//    public static final RuntimeException INDEX_OUT_OF_BOUND = new IndexOutOfBoundsException();
//
//    /**
//     * 请求的地址太新了，扩容一个页之后，依然小于请求地址。说明获取地址时，没有考虑并发
//     */
//    public static final RuntimeException REQUEST_ADDRESS_TOO_BIG = new RuntimeException("HIPPO-ERROR-CODE-500");
//
//    public static final OutOfMemory OUT_OF_MEMORY = new OutOfMemory("out of memory");
//    public static final OutOfMemory OUT_OF_MEMORY_MAX_PAGE = new OutOfMemory("out of memory. reason: Max Page");
//
//    public static final BeanTooBig BEAN_TOO_BIG = new BeanTooBig();
//}

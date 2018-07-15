//package pers.fq.hippo.store.core;
//
//import java.nio.ByteBuffer;
//
///**
// * 1、绝不可能并发写一个offset
// * 2、不同的offset可以并发写
// *
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/19
// */
//public class Page {
//
//    public final ByteBuffer buffer;
//
//    public Page(ByteBuffer buffer) {
//        this.buffer = buffer;
//    }
//
//    public Bean read(int offset) {
//        return Bean.parseBean(buffer.duplicate(), offset);
//    }
//
//    public void write(int position, Bean bean) {
//        ByteBuffer buf = buffer.duplicate();
//        buf.position(position);
//        buf.put(bean.toWritableByteBuffer());
//    }
//}
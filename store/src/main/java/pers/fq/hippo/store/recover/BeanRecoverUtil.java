//package pers.fq.hippo.store.recover;
//
//import pers.fq.hippo.common.ByteUtil;
//import pers.fq.hippo.store.core.Bean;
//import pers.fq.hippo.store.tag.Nullable;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.nio.ByteBuffer;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/7/26
// */
//public class BeanRecoverUtil {
//
//
//    // 测试写2次Bean，如果第二次已经超过buffer了，那第二次会写入一半吗？？？ 超过buffer的部分下次写？？？
//
//    /**
//     * 从流中读取一个bean
//     *
//     * @param inputStream
//     * @return
//     * @throws IOException
//     */
//    @Nullable
//    public static Bean readBean(InputStream inputStream) throws IOException {
//        // 获取bean的字节数
//        byte[] lenByte = new byte[4];
//        int readByte = inputStream.read(lenByte);
//
//        // 表示流已经读完了
//        if (readByte == -1) {
//            return null;
//        }
//
//        int len = ByteUtil.byte2Int(lenByte);
//        byte[] beanByte = new byte[len];
//        inputStream.read(beanByte);
//
//        Bean bean = Bean.parseBean(ByteBuffer.wrap(beanByte), 0);
//
//        return bean;
//    }
//
//    public static void writeBean(OutputStream outputStream, Bean bean) throws IOException {
//        outputStream.write(ByteUtil.int2Byte(bean.size()));
//        outputStream.write(bean.toWritableByteBuffer().array());
//    }
//}

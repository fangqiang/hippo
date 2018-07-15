package pers.fq.hippo.common;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/22
 */
public class ByteBufferPool {

    public  final int size;

    // TODO 监控队列的大小看看是否够用
    private ConcurrentLinkedQueue<ReusableByteBuffer> pool = new ConcurrentLinkedQueue();

    public ByteBufferPool(int size, int init) {
        this.size = size;

        for (int i = 0; i < init; i++) {
            ReusableByteBuffer buffer = new ReusableByteBuffer(this, ByteBuffer.allocate(size), true);
            pool.add(buffer);
        }
    }

    public ReusableByteBuffer acquire(){
        ReusableByteBuffer ret =  pool.poll();
        return ret!=null? ret : new ReusableByteBuffer(this, ByteBuffer.allocate(size),  false);
    }

    void release(ReusableByteBuffer reusableByteBuffer){
        reusableByteBuffer.buffer.clear();
        pool.add(reusableByteBuffer);
    }
}

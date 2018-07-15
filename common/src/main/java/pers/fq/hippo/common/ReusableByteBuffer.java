package pers.fq.hippo.common;

import java.nio.ByteBuffer;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/22
 */
public class ReusableByteBuffer implements AutoCloseable{
    public final ByteBufferPool pool;
    public final ByteBuffer buffer;
    public final boolean isBorrow;

    public ReusableByteBuffer(ByteBufferPool pool, ByteBuffer buffer, boolean isBorrow) {
        this.pool = pool;
        this.buffer = buffer;
        this.isBorrow = isBorrow;
    }

    @Override
    public void close() throws Exception {
        if(isBorrow){
            pool.release(this);
        }
    }

    public void put(byte b){
        buffer.put(b);
    }

    public void putInt(int i){
        buffer.putInt(i);
    }

    public void putLong(long l){
        buffer.putLong(l);
    }

    public int position(){
        return buffer.position();
    }

    public void position(int pos){
        buffer.position(pos);
    }

    public ByteBuffer put(ByteBuffer src){
        return buffer.put(src);
    }

    public ByteBuffer put(byte[] src){
        return buffer.put(src);
    }
}

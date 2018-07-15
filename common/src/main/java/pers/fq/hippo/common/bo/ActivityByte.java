package pers.fq.hippo.common.bo;

import pers.fq.hippo.common.ByteUtil;
import pers.fq.hippo.common.tag.Checked;
import pers.fq.hippo.common.tag.SideEffect;
import pers.fq.hippo.common.tag.SortedAsc;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * @Description:  业务类Activity的序列化后的格式
 * @author: fang
 * @date: Created by on 18/11/17
 */
public class ActivityByte {

    public final byte[] source;
    public final int startPos;
    public final int length;

    @Checked
    public static int fillIn(@SideEffect ByteBuffer source, @SortedAsc TreeMap<Integer, String> data, long time){

        int startPos = source.position();

        // 写入时间
        source.putLong(time);
        source.putInt(data.size());

        // 设置写的buf的起始位置
        ByteBuffer writeValue = source.duplicate();
        writeValue.position(source.position() + data.size() * 8);

        // 写入
        for (Map.Entry<Integer, String> entry : data.entrySet()) {
            Integer key = entry.getKey();
            String val = entry.getValue();
            // 写入索引
            source.putInt(key);
            source.putInt(writeValue.position());

            // 写入值
            byte[] valBytes = val.getBytes();
            writeValue.putInt(valBytes.length);
            writeValue.put(valBytes);
        }

        // 将最后的指针赋值给source
        source.position(writeValue.position());

        // 返回本次填充的字节数
        return source.position() - startPos;
    }

    public ActivityByte(byte[] source, int startPos, int length){
        this.source = source;
        this.startPos = startPos;
        this.length = length;
    }

    public long getTime(){
        return ByteUtil.byte2Long(source, startPos);
    }

    @Checked
    public Map<Integer, String> getColumns(List<Integer> cols){
        if(cols.isEmpty()){
            return Collections.emptyMap();
        }

        Map<Integer, String> result = new HashMap<>();

        ByteBuffer iterCopy = ByteBuffer.wrap(source, startPos, length);
        ByteBuffer readCopy = ByteBuffer.wrap(source, startPos, length);
        iterCopy.position(iterCopy.position()+8); // 跳过时间


        int columnIdx = 0;

        int idxSize = iterCopy.getInt();
        for (int i = 0; i < idxSize; i++) {
            int colId = iterCopy.getInt();

            while(cols.get(columnIdx) < colId){
                columnIdx++;
                if(columnIdx >= cols.size()){
                    return result;
                }
            }

            if(cols.get(columnIdx) == colId){
                int colStartPos = iterCopy.getInt();
                readCopy.position(colStartPos);
                int valLen = readCopy.getInt();
                byte[] val = new byte[valLen];
                readCopy.get(val);
                result.put(colId, new String(val));

                columnIdx++;

                if(columnIdx >= cols.size()){
                    return result;
                }
            }else{
                // 跳过地址
                iterCopy.position(iterCopy.position()+4);
            }
        }

        return result;
    }

    public Map<Integer, String> toMap(){

        ByteBuffer iterCopy = ByteBuffer.wrap(source, startPos, length);
        ByteBuffer readCopy = ByteBuffer.wrap(source, startPos, length);
        iterCopy.position(iterCopy.position()+8); // 跳过时间

        Map<Integer, String> result = new HashMap<>();

        int idxSize = iterCopy.getInt();
        for (int i = 0; i < idxSize; i++) {
            int colId = iterCopy.getInt();
            int colStartPos = iterCopy.getInt();
            readCopy.position(colStartPos);
            int valLen = readCopy.getInt();
            byte[] val = new byte[valLen];
            readCopy.get(val);
            result.put(colId, new String(val));
        }

        return result;
    }
}

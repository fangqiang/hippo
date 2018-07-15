package pers.fq.hippo.common.bo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fq.hippo.common.ByteBufferPool;
import pers.fq.hippo.common.ByteBufferUtil;
import pers.fq.hippo.common.ReusableByteBuffer;
import pers.fq.hippo.common.Utils;
import pers.fq.hippo.common.monitor.Monitor;
import pers.fq.hippo.common.tag.Checked;
import pers.fq.hippo.common.tag.Nullable;
import pers.fq.hippo.common.tag.SortedAsc;
import pers.fq.hippo.common.tag.SortedDesc;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * List<activity>的ByteBuffer格式，用于在数组级别操作数据
 *
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/23
 */
public class ActivityHandler {

    private static final Logger logger = LoggerFactory.getLogger(ActivityHandler.class);

    public static final int MAX_SIZE = 1024 * 1024;

    static ByteBufferPool bufferPool = new ByteBufferPool(MAX_SIZE, 64);


    /**
     * @param bytes : 排序的activity数组（按时间逆序）
     * @param columns : 要获取的列，如果为空则获取所有列
     * @param limit : 最后获取多少行
     * @param startTime : 小于最小时间的数据会被删除
     * @param endTime : 大于最小时间的数据会被删除
     * @return
     */
    @Nullable
    @Checked
    public static List<Activity> getWithCondition(byte[] bytes, boolean allColumn, Set<String> columns, int limit, long startTime, long endTime){

        @SortedDesc
        List<ActivityByte> rows = toActivityByte(bytes);
        List<Activity> ret = new LinkedList();
        int count = 0;
        try {

            @SortedAsc
            List<Integer> sortedColumn = toHashedSortedSet(columns);

            for (ActivityByte row : rows) {
                long time = row.getTime();
                if (time > endTime) {
                    continue; // 比最大时间大，则跳过
                }
                if (time < startTime) {
                    break; // 如果遇到时间比最小时间还小，则直接退出（因为后面的时间一定更小）
                }

                Map<Integer, String> kvs = allColumn ?
                        row.toMap():
                        row.getColumns(sortedColumn);

                // 将key由hash值反解析成字符串
                HashMap<String, String> ssHashMap = toStrStrMap(kvs);
                ret.add(new Activity(time, ssHashMap));

                count++;
                if (count >= limit) {
                    // 如果达到limit条数，则直接退出
                    break;
                }
            }
        }catch (BufferOverflowException e){
            logger.info("BufferOverflowException: now size: {}", count);
        }
        return ret;
    }

    @Checked
    private static List<Integer> toHashedSortedSet(Set<String> set){
        if(set.isEmpty()){
            return Collections.EMPTY_LIST;
        }

        List<Integer> ret = new ArrayList(set.size());

        for (String s : set) {
            ret.add(hashKey(s));
        }
        Collections.sort(ret);
        return ret;
    }

    /**
     * 合并2批业务数据
     *
     * @param retain: 最多保留多少条
     * @param expireBorder : 小于expireBorder的activity将被抛弃
     * @return
     */
    @Nullable
    @Checked
    public static byte[] merge(@Nullable byte [] oldVal, byte [] curVal, int retain, long expireBorder){
        List<ActivityByte> old = oldVal != null ? toActivityByte(oldVal) : null;
        List<ActivityByte> cur = toActivityByte(curVal);
        try(ReusableByteBuffer buffer = bufferPool.acquire()){
            return merge(buffer.buffer, old, cur, retain, expireBorder);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 将一个byte数组转成多个activity对象
     *
     * 存储结构如下
     * ----------------------
     * 数组长度
     * activity长度 + activity
     * activity长度 + activity
     * ----------------------
     */
    @Checked
    private static List<ActivityByte> toActivityByte(byte [] bytes){
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int rowSize = buffer.getInt();

        List<ActivityByte> rows = new ArrayList<>(rowSize);

        for (int i = 0; i < rowSize; i++) {
            int rowLen = buffer.getInt();
            rows.add(new ActivityByte(buffer.array(), buffer.position(), rowLen));
            buffer.position(buffer.position()+rowLen);
        }

        return rows;
    }

    @Checked
    public static byte[] merge(ByteBuffer buffer, @Nullable List<ActivityByte> old, List<ActivityByte> cur, int retain, long expireBorder){

        int startPos = buffer.position();

        // 先占4个字节
        buffer.putInt(0);

        Iterator<ActivityByte> curIter = cur.iterator();
        Iterator<ActivityByte> oldIter = old != null ? old.iterator() : Collections.emptyIterator();

        ActivityByte c = curIter.hasNext() ? curIter.next() : null;
        ActivityByte o = oldIter.hasNext() ? oldIter.next() : null;

        ActivityByte latest = null;

        int rowCount = 0;

        try {
            for (int i = 0; i < retain; i++) {
                if (c != null && o != null) {
                    if(c.getTime() >= o.getTime()){
                        latest = c;
                        c = curIter.hasNext() ? curIter.next() : null;
                    }else{
                        latest = o;
                        o = oldIter.hasNext() ? oldIter.next() : null;
                    }
                }else if(o != null) {
                    latest = o;
                    o = oldIter.hasNext() ? oldIter.next() : null;
                }else if(c != null) {
                    latest = c;
                    c = curIter.hasNext() ? curIter.next() : null;
                }else{
                    break;
                }

                if (latest.getTime() <= expireBorder) {
                    break;
                }

                buffer.putInt(latest.length);
                buffer.put(latest.source, latest.startPos, latest.length);
                rowCount++;
            }
        }catch (BufferOverflowException e){
            logger.info("BufferOverflowException: now size: {}", rowCount);
        }

        if(rowCount == 0){
            return null;
        }else {

            Monitor.value("drop_size", cur.size() + (old == null ? 0 : old.size()) - rowCount);

            // 写入数组长度
            int pos = buffer.position();
            buffer.position(startPos);
            buffer.putInt(rowCount);
            buffer.position(pos);

            return ByteBufferUtil.copyUsedArray(buffer, 0, buffer.position());
        }
    }


    /**
     * 业务数据转换成字节数组
     *
     * @param table
     * @return
     */
    @Checked
    public static byte[] toBytes(@SortedDesc List<Activity> table){

        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);

        // 记录行数
        buffer.putInt(table.size());

        for (Activity row : table) {
            int startPos = buffer.position();
            buffer.putInt(-1); // 占个位置

            TreeMap<Integer, String> intStringMap = toIntStringMap(row.map);
            int rowSize = ActivityByte.fillIn(buffer, intStringMap, row.time);
            int endPos = buffer.position();

            // 将每行长度填充
            buffer.position(startPos);
            buffer.putInt(rowSize);
            buffer.position(endPos);
        }

        return ByteBufferUtil.copyUsedArray(buffer, 0, buffer.position());
    }

    @Checked
    static HashMap<String,String> toStrStrMap(Map<Integer, String> map){
        if(map.isEmpty()){
            return Utils.SINGLTION_HASHMAP;
        }
        HashMap<String,String> dest = new HashMap<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            String key = getKeyByHash(entry.getKey());
            dest.put(key, entry.getValue());
        }
        return dest;
    }

    @Checked
    static TreeMap<Integer,String> toIntStringMap(HashMap<String, String> map){
        TreeMap<Integer,String> dest = new TreeMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            int key = hashKey(entry.getKey());
            dest.put(key, entry.getValue());
        }
        return dest;
    }

    /**
     * 缓存key -> hash(key)的映射，提高存取效率
     */
    static ConcurrentHashMap<Integer, String> hashKey = new ConcurrentHashMap(1024);

    @Checked
    private static String getKeyByHash(Integer hash){
        return hashKey.get(hash);
    }

    @Checked
    private static int hashKey(String key){
        int hash = key.hashCode();
        if( ! hashKey.containsKey(hash)){
            hashKey.put(hash, key);
        }
        return hash;
    }
}

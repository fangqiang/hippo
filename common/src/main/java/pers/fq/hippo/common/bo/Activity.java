package pers.fq.hippo.common.bo;

import pers.fq.hippo.common.*;
import pers.fq.hippo.common.tag.Nullable;

import java.util.HashMap;

/**
 * 核心数据结构，一个activity
 *
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/28
 */
public class Activity implements Comparable{

    /**
     * eventOccurTime
     */
    public long time;

    /**
     * 其他字段
     */
    public HashMap<String, String> map;

    /**
     *  为了使用kryo序列化
     */
    public Activity() { }

    public Activity(long time, HashMap<String, String> map) {

        Assert.check(time > 0, "time mush time > 0 than 0");
        Assert.check(map != null, "map is null"); // TODO 过滤条件哪里可能为空

        this.time = time;
        this.map = map;
    }

    @Nullable
    public String get(String key){
        return map.get(key);
    }

    /**
     * 按时间逆序
     */
    @Override
    public int compareTo(Object o) {
        long t1 = ((Activity)o).time;
        if(time > t1){
            return -1;
        }else if(time == t1){
            return 0;
        }else{
            return 1;
        }
    }
}

package pers.fq.hippo.common.parameter.req;

import org.apache.commons.lang3.StringUtils;
import pers.fq.hippo.common.Assert;
import pers.fq.hippo.common.bo.Activity;
import pers.fq.hippo.common.bo.ActivityHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/11/17
 */
public class AppendRequest {

    String key;

    /**
     * 事件
     */
    ArrayList<Activity> activities;

    /**
     * 允许最旧的时间，小于这个时间的数据将被删除
     */
    long oldestTime;

    /**
     * 行数上限
     */
    int limit;

    /**
     * 判断key是否存在
     */
    boolean onlyIfExist;

    /**
     *  为了使用kryo序列化
     */
    public AppendRequest(){}

    private AppendRequest(String key, ArrayList<Activity> activities, long oldestTime, int limit, boolean onlyIfExist) {
        this.key = key;
        this.activities = activities;
        this.oldestTime = oldestTime;
        this.limit = limit;
        this.onlyIfExist = onlyIfExist;
    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<Activity> activities) {
        this.activities = activities;
    }

    public boolean onlyIfExist() {
        return onlyIfExist;
    }

    public void setOnlyIfExist(boolean onlyIfExist) {
        this.onlyIfExist = onlyIfExist;
    }

    public static Builder getBuilder(){
        return new Builder();
    }

    public static class Builder{

        String key;

        ArrayList<Activity> activityList = new ArrayList<>();
        long oldestTime;
        int limit;
        boolean onlyIfExist;

        public Builder addAllActivity(List<Activity> activityList) {
            for (Activity activity : activityList) {
               addActivity(activity);
            }
            return this;
        }
        public Builder addActivity(Activity activity) {
            Assert.check( ! activity.map.isEmpty(), "invalid activity");
            activityList.add(activity);
            return this;
        }

        public Builder setOldestTime(long oldestTime) {
            this.oldestTime = oldestTime;
            return this;
        }

        public Builder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public Builder onlyIfExist(boolean onlyIfExist){
            this.onlyIfExist = onlyIfExist;
            return this;
        }

        public AppendRequest build(){
            Assert.check(StringUtils.isNoneBlank(key), "key not valid");
            Assert.check(limit >0 && limit <= 1000, "limit not valid");
            Assert.check(oldestTime >0, "oldestTime not valid");
            Assert.check( ! activityList.isEmpty(), "activityList is valid");

            Collections.sort(activityList);

            return new AppendRequest(key, activityList, oldestTime, limit, onlyIfExist);
        }
    }

    public long getOldestTime() {
        return oldestTime;
    }

    public void setOldestTime(long oldestTime) {
        this.oldestTime = oldestTime;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getKey() {
        return key;
    }
}

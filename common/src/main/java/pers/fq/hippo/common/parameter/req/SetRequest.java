package pers.fq.hippo.common.parameter.req;

import pers.fq.hippo.common.parameter.ConditionItem;
import org.apache.commons.lang3.StringUtils;
import pers.fq.hippo.common.Assert;
import pers.fq.hippo.common.tag.Nullable;

import java.util.*;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/11/17
 */
public class SetRequest {

    /**
     * 获取的key
     */
    String key;

    /**
     * 限制行数
     */
    int limit;

    /**
     * 起止时间
     */
    long startTime;
    long endTime;

    /**
     * 过滤条件, nullable
     */
    String expression;
    ArrayList<ConditionItem> conditions;

    /**
     * 计算类型
     */
    String calColumn;

    /**
     *  为了使用kryo序列化
     */
    public SetRequest(){}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCalColumn() {
        return calColumn;
    }

    public void setCalColumn(String calColumn) {
        this.calColumn = calColumn;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public @Nullable String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public @Nullable ArrayList<ConditionItem> getConditions() {
        return conditions;
    }

    public void setConditions(ArrayList<ConditionItem> conditions) {
        this.conditions = conditions;
    }

    /**
     * 返回过滤条件
     */
    public HashSet<String> getConditionField(){
        HashSet<String> hashSet = new HashSet();

        if(conditions == null){
            return hashSet;
        }

        for (ConditionItem condition : conditions) {
            hashSet.add(condition.leftColumn);
        }

        return hashSet;
    }

    public static ComputeRequestBuilder getBuilder() {
        return new ComputeRequestBuilder();
    }

    public static final class ComputeRequestBuilder {
        String key;
        int limit;
        long startTime;
        long endTime;
        String expression;
        ArrayList<ConditionItem> conditions;
        String calColumn;

        private ComputeRequestBuilder() { }

        public ComputeRequestBuilder setKey(String key) {
            this.key = key;
            return this;
        }

        public ComputeRequestBuilder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public ComputeRequestBuilder setTimeRange(long startTime, long endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
            return this;
        }

        public ComputeRequestBuilder setFilter(String expression, List<ConditionItem> conditions) {
            this.expression = expression;
            if(conditions instanceof ArrayList){
                this.conditions = (ArrayList<ConditionItem>) conditions;
            }else{
                this.conditions = new ArrayList<>(conditions);
            }
            return this;
        }

        public ComputeRequestBuilder setCalColumn(String calColumn) {
            this.calColumn = calColumn;
            return this;
        }

        public SetRequest build() {
            Assert.check(StringUtils.isNotBlank(key), "key is empty");
            Assert.check(StringUtils.isNotBlank(calColumn), "calColumn is empty");
            Assert.check(limit > 0 && limit <= 1000 , "limit is invalid");
            Assert.check(startTime >= 0 && startTime < endTime, "time range is invalid");

            SetRequest setRequest = new SetRequest();
            setRequest.key = this.key;
            setRequest.setLimit(limit);
            setRequest.setStartTime(startTime);
            setRequest.setEndTime(endTime);
            setRequest.setExpression(expression);
            setRequest.setConditions(conditions);
            setRequest.setCalColumn(calColumn);

            return setRequest;
        }
    }
}

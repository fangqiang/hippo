package pers.fq.hippo.common.parameter.req;

import pers.fq.hippo.common.Utils;
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
public class QueryRequest {

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
     * 需要的列，null表示取所有列
     */
    HashSet<String> columns;

    /**
     * 过滤条件
     */
    String expression;
    ArrayList<ConditionItem> conditions;

    /**
     * 是否需要压缩
     */
    boolean compress;

    /**
     *  为了使用kryo序列化
     */
    public QueryRequest(){}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public @Nullable HashSet<String> getColumns() {
        return columns;
    }

    public boolean needAllColumn(){
        return columns == null;
    }

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

    public void setColumns(HashSet<String> columns) {
        this.columns = columns;
    }

    public @Nullable String getExpression() {
        return expression;
    }

    public  void setExpression(String expression) {
        this.expression = expression;
    }

    public @Nullable ArrayList<ConditionItem> getConditions() {
        return conditions;
    }

    public void setConditions(ArrayList<ConditionItem> conditions) {
        this.conditions = conditions;
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

    public static QueryRequestBuilder getBuilder() {
        return new QueryRequestBuilder();
    }

    public static final class QueryRequestBuilder {
        String key;
        int limit;
        long startTime;
        long endTime;
        HashSet<String> columns;
        String expression;
        ArrayList<ConditionItem> conditions;
        boolean compress;

        private QueryRequestBuilder() {
        }

        public static QueryRequestBuilder aQueryRequest() {
            return new QueryRequestBuilder();
        }

        public QueryRequestBuilder setKey(String key) {
            this.key = key;
            return this;
        }

        public QueryRequestBuilder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public QueryRequestBuilder setTimeRange(long startTime, long endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
            return this;
        }

        public QueryRequestBuilder setColumns(HashSet<String> columns) {
            this.columns = columns;
            return this;
        }

        public QueryRequestBuilder setFilter(String expression, List<ConditionItem> conditions) {
            this.expression = expression;
            if(conditions instanceof ArrayList){
                this.conditions = (ArrayList<ConditionItem>) conditions;
            }else{
                this.conditions = new ArrayList<>(conditions);
            }
            return this;
        }

        public QueryRequestBuilder setCompress(boolean compress) {
            this.compress = compress;
            return this;
        }

        public QueryRequest build() {
            Assert.check(StringUtils.isNotBlank(key), "key is empty");
            Assert.check(limit > 0 && limit <= 1000 , "limit is invalid");
            Assert.check(startTime >= 0 && startTime < endTime, "time range is invalid");

            QueryRequest queryRequest = new QueryRequest();
            queryRequest.key = this.key;
            queryRequest.setLimit(limit);
            queryRequest.setStartTime(startTime);
            queryRequest.setEndTime(endTime);
            queryRequest.setColumns(columns);
            queryRequest.setExpression(expression);
            queryRequest.setConditions(conditions);
            queryRequest.setCompress(compress);

            return queryRequest;
        }
    }
}

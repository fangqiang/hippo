package pers.fq.hippo.biz;

import pers.fq.hippo.common.Utils;
import pers.fq.hippo.common.bo.Activity;
import pers.fq.hippo.common.bo.ActivityHandler;
import pers.fq.hippo.common.parameter.*;
import pers.fq.hippo.common.parameter.req.*;
import pers.fq.hippo.common.parameter.resp.*;
import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fq.hippo.common.monitor.Monitor;
import pers.fq.hippo.common.tag.Checked;
import pers.fq.hippo.common.tag.Nullable;
import pers.fq.hippo.common.tag.SideEffect;
import pers.fq.hippo.storage.Cache;

import java.util.*;
import java.util.concurrent.*;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/8/19
 */
public class Meddler {
    private static final Logger logger = LoggerFactory.getLogger(Meddler.class);

    private static DataCache dataCache = new DataCache();

    static ThreadPoolExecutor executors = new ThreadPoolExecutor(
            Utils.STANDARD_THREAD_SIZE, Utils.STANDARD_THREAD_SIZE,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(128),
            new ThreadPoolExecutor.CallerRunsPolicy());

    static {
        Monitor.regist(() -> {
            Monitor.value("activeThread", executors.getActiveCount());
            Monitor.value("handleQueueSize", executors.getQueue().size());
        });
    }

    @Checked
    public AppendResponse append(Cache cache, AppendRequest appendRequest) {
        ArrayList<Activity> activities = appendRequest.getActivities();
        byte[] curData = ActivityHandler.toBytes(activities);

        cache.append(appendRequest.getKey(), curData, (byte[] oldData, byte[] newData) -> {
            // 依赖历史数据，且历史数据不存在，放弃
            if (appendRequest.onlyIfExist() && oldData == null) {
                return null;
            }

            int limit = appendRequest.getLimit();
            long oldestTime = appendRequest.getOldestTime();
            @Nullable
            byte[] finalValue = ActivityHandler.merge(oldData, newData, limit, oldestTime);

            return finalValue;
        });

        return AppendResponse.SINGLETON;
    }

    public QueryResponse query(Cache cache, QueryRequest queryRequest) {

        String key = queryRequest.getKey();
        int limit = queryRequest.getLimit();
        long oldestTime = queryRequest.getStartTime();
        long latestTime = queryRequest.getEndTime();
        HashSet<String> retColumn = queryRequest.getColumns();
        Set<String> conditionField = queryRequest.getConditionField();

        boolean compress = queryRequest.isCompress();

        boolean needAllColumn = queryRequest.needAllColumn();

        // 如果需要所有列，那就不需要在查看需要什么列了
        Set<String> columns;
        if (needAllColumn) {
            columns = Collections.EMPTY_SET;
        } else {
            columns = new HashSet();
            columns.addAll(retColumn);
            columns.addAll(conditionField);
        }

        @Nullable
        byte[] oldData = cache.get(key);

        List<Activity> activities = oldData == null ?
                Collections.emptyList() :
                ActivityHandler.getWithCondition(oldData, needAllColumn, columns, limit, oldestTime, latestTime);

        @Nullable String expression = queryRequest.getExpression();
        @Nullable ArrayList<ConditionItem> conditions = queryRequest.getConditions();
        if(expression != null) {
            filterRow(expression, conditions, activities);
        }

        // 删除不需要的列
        if( ! needAllColumn) {
            conditionField.removeAll(retColumn);
            if(conditionField.size() > 0) {
                for (Activity activity : activities) {
                    for (String s : conditionField) {
                        activity.map.remove(s);
                    }
                }
            }
        }

        return compress ?
                QueryResponse.instanceWithCompress(activities):
                QueryResponse.instance(activities);
    }

    @Checked
    public SetResponse computeSet(Cache cache, SetRequest setRequest) {

        String key = setRequest.getKey();
        int limit = setRequest.getLimit();
        long oldestTime = setRequest.getStartTime();
        long latestTime = setRequest.getEndTime();
        String calColumn = setRequest.getCalColumn();

        Set<String> columns = setRequest.getConditionField();
        // 加上计算字段
        columns.add(calColumn);

        @Nullable
        byte[] oldData = getCachedValue(key, cache);

        List<Activity> activities = oldData == null ?
                Collections.emptyList() :
                ActivityHandler.getWithCondition(oldData, false, columns, limit, oldestTime, latestTime);

        @Nullable String expression = setRequest.getExpression();
        @Nullable ArrayList<ConditionItem> conditions = setRequest.getConditions();
        if(expression != null) {
            filterRow(expression, conditions, activities);
        }

        HashSet<String> set = new HashSet();
        for (Activity activity : activities) {
            set.add(activity.get(calColumn));
        }

        return new SetResponse(set);
    }

    @Checked
    public CountResponse computeCount(Cache cache, CountRequest countRequest) {

        String key = countRequest.getKey();
        int limit = countRequest.getLimit();
        long oldestTime = countRequest.getStartTime();
        long latestTime = countRequest.getEndTime();

        Set<String> columns = countRequest.getConditionField();

        @Nullable
        byte[] oldData = getCachedValue(key, cache);

        List<Activity> activities = oldData == null ?
                Collections.emptyList() :
                ActivityHandler.getWithCondition(oldData, false, columns, limit, oldestTime, latestTime);

        @Nullable String expression = countRequest.getExpression();
        @Nullable ArrayList<ConditionItem> conditions = countRequest.getConditions();
        if(expression != null) {
            filterRow(expression, conditions, activities);
        }

        return new CountResponse(activities.size());
    }

    public RemoveResponse remove(Cache cache, RemoveRequest appendRequest) {
        cache.remove(appendRequest.getKey());
        return new RemoveResponse();
    }

    private void filterRow(String expression, ArrayList<ConditionItem> conditions, @SideEffect List<Activity> activities) {
        CompiledExpression logicExp = ExpressService.get(expression);

        Iterator<Activity> iterator = activities.iterator();
        while (iterator.hasNext()) {
            try {
                Activity act = iterator.next();
                boolean result = matchCondition(act, logicExp, conditions);
                if (!result) {
                    iterator.remove();
                }
            } catch (Exception e) {
                logger.error("filter error", e);
            }
        }
    }

    @Checked
    private boolean matchCondition(Activity activity, CompiledExpression logicExp, List<ConditionItem> conditionItems) {
        Map<String, Boolean> bools = new HashMap<>(conditionItems.size());
        for (ConditionItem conditionItem : conditionItems) {
            @Nullable
            String leftValue = activity.get(conditionItem.leftColumn);
            boolean bool = true; //TODO Comparator.operate(leftValue, conditionItem.rightValue, conditionItem.leftType, conditionItem.operatorType);
            bools.put(conditionItem.name, bool);
        }

        return (boolean) MVEL.executeExpression(logicExp, bools);
    }

    @Nullable
    private byte[] getCachedValue(String key, Cache cache){
        byte[] bytes = dataCache.get(key);
        if(bytes!=null){
            return bytes;
        }else{
            byte[] curBytes = cache.get(key);
            if(curBytes != null){
                dataCache.put(key, curBytes);
                return curBytes;
            }else{
                return null;
            }
        }
    }
}

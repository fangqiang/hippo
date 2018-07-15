package pers.fq.hippo.common.parameter.resp;


import pers.fq.hippo.common.LZ4Utils;
import pers.fq.hippo.common.SerialUtils;
import pers.fq.hippo.common.bo.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/11/29
 */
public class QueryResponse {

    List<Activity> activities;

    byte[] value;

    int sourceSize;

    boolean compress;

    public static QueryResponse instance(List<Activity> activities){
        QueryResponse queryResponse = new QueryResponse();
        queryResponse.activities = activities;
        queryResponse.compress = false;
        return queryResponse;
    }

    public static QueryResponse instanceWithCompress(List<Activity> activities){
        QueryResponse queryResponse = new QueryResponse();

        byte[] bytes = SerialUtils.obj2Byte(activities);
        byte[] compressed = LZ4Utils.compress(bytes);

        queryResponse.compress = true;
        queryResponse.sourceSize = bytes.length;
        queryResponse.value = compressed;
        return queryResponse;
    }

    public QueryResponse() {

    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public int getSourceSize() {
        return sourceSize;
    }

    public void setSourceSize(int sourceSize) {
        this.sourceSize = sourceSize;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public void deCompressIfNeed(){
        if(compress){
            byte[] decompress = LZ4Utils.decompress(value, sourceSize);
            activities = SerialUtils.byte2Obj(decompress, ArrayList.class);
        }
    }
    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }
}

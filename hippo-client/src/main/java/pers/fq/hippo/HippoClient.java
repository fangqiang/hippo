package pers.fq.hippo;

import pers.fq.hippo.common.parameter.*;
import pers.fq.hippo.common.parameter.req.*;
import pers.fq.hippo.common.parameter.resp.*;
import pers.fq.hippo.common.SerialUtils;
import pers.fq.hippo.transporter.Request;
import pers.fq.hippo.transporter.Response;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/14
 */
public class HippoClient extends MutiClient{

    public HippoClient(String zk, String zkPath, int port) throws Exception {
        super(zk, zkPath, port);
    }

    public AppendResponse send(AppendRequest appendRequest, int timeout) throws Exception {
        Request request = new Request(appendRequest.getKey(), SerialUtils.obj2Byte(appendRequest), RequestConst.APPEND);
        send(request).get(timeout, TimeUnit.MILLISECONDS);
        return null;
    }

    public RemoveResponse send(RemoveRequest removeRequest, int timeout) throws Exception {
        Request request = new Request(removeRequest.getKey(), SerialUtils.obj2Byte(removeRequest), RequestConst.REMOVE);
        send(request).get(timeout, TimeUnit.MILLISECONDS);
        return null;
    }

    public CountResponse send(CountRequest countRequest, int timeout) throws Exception {
        Request request = new Request(countRequest.getKey(), SerialUtils.obj2Byte(countRequest), RequestConst.COMPUTE_COUNT);
        Response response = send(request).get(timeout, TimeUnit.MILLISECONDS);
        return SerialUtils.byte2Obj(response.getResult(), CountResponse.class);
    }

    public SetResponse send(SetRequest setRequest, int timeout) throws Exception {
        Request request = new Request(setRequest.getKey(), SerialUtils.obj2Byte(setRequest), RequestConst.COMPUTE_SET);
        Response response = send(request).get(timeout, TimeUnit.MILLISECONDS);
        return SerialUtils.byte2Obj(response.getResult(), SetResponse.class);
    }

    public QueryResponse send(QueryRequest queryRequest, int timeout) throws Exception {
        Request request = new Request(queryRequest.getKey(), SerialUtils.obj2Byte(queryRequest), RequestConst.QUERY);
        Response response = send(request).get(timeout, TimeUnit.MILLISECONDS);
        QueryResponse queryResponse = SerialUtils.byte2Obj(response.getResult(), QueryResponse.class);
        // 对数据解压
        queryResponse.deCompressIfNeed();
        return queryResponse;
    }
}
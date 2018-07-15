package pers.fq.hippo;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fq.hippo.biz.Meddler;
import pers.fq.hippo.common.SerialUtils;
import pers.fq.hippo.common.monitor.Monitor;
import pers.fq.hippo.common.parameter.RequestConst;
import pers.fq.hippo.common.parameter.req.*;
import pers.fq.hippo.common.parameter.resp.*;
import pers.fq.hippo.storage.Cache;
import pers.fq.hippo.transporter.Request;
import pers.fq.hippo.transporter.RequestHandler;
import pers.fq.hippo.transporter.Response;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/28
 */
public class RequestHandlerImpl implements RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerImpl.class);

    private Cache store;

    private Meddler meddler = new Meddler();

    public RequestHandlerImpl(Cache store) {
        this.store = store;
    }

    @Override
    public void handler(Channel channel, Request request) {
        try {
            Response response = handler(store, request);
            channel.writeAndFlush(response.toBytes());
        }catch (Exception e){
            logger.error("meddle_failed", e);
        }
    }

    private Response handler(Cache cache, Request request) {
        if(request.getType() == RequestConst.COMPUTE_COUNT){
            // 统计tps
            Monitor.tps("tps", 1, "operType", "count");

            CountRequest countRequest = SerialUtils.byte2Obj(request.getData(), CountRequest.class);
            CountResponse countResponse = meddler.computeCount(cache, countRequest);
            return new Response(request.getUuid(), SerialUtils.obj2Byte(countResponse));
        }else if(request.getType() == RequestConst.COMPUTE_SET){
            // 统计tps
            Monitor.tps("tps", 1, "operType", "set");

            SetRequest setRequest = SerialUtils.byte2Obj(request.getData(), SetRequest.class);
            SetResponse setResponse = meddler.computeSet(cache, setRequest);
            return new Response(request.getUuid(), SerialUtils.obj2Byte(setResponse));
        }else if(request.getType() == RequestConst.QUERY){
            // 统计tps
            Monitor.tps("tps", 1, "operType", "query");

            QueryRequest queryRequest = SerialUtils.byte2Obj(request.getData(), QueryRequest.class);
            QueryResponse queryResponse = meddler.query(cache, queryRequest);
            return new Response(request.getUuid(), SerialUtils.obj2Byte(queryResponse));
        }else if(request.getType() == RequestConst.APPEND){
            // 统计tps
            Monitor.tps("tps", 1, "operType", "append");

            AppendRequest appendRequest = SerialUtils.byte2Obj(request.getData(), AppendRequest.class);
            AppendResponse appendResponse = meddler.append(cache, appendRequest);
            return new Response(request.getUuid(), null);
        }else if(request.getType() == RequestConst.REMOVE){
            // 统计tps
            Monitor.tps("tps", 1, "operType", "remove");

            RemoveRequest removeRequest = SerialUtils.byte2Obj(request.getData(), RemoveRequest.class);
            RemoveResponse removeResponse = meddler.remove(cache, removeRequest);
            return new Response(request.getUuid(), null);
        }else{
            logger.error("unknown type: {}", request.getType());
            return null;
        }
    }
}

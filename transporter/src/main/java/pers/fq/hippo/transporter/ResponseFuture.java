package pers.fq.hippo.transporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fq.hippo.common.monitor.Monitor;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/14
 */
public class ResponseFuture extends FutureTask implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ResponseFuture.class);

    private static final ConcurrentHashMap<Long, ResponseFuture> FUTURES = new ConcurrentHashMap<Long, ResponseFuture>();

    Request request;
    Response response;

    /**
     * 空任务为了初始化future
     */
    private static final Callable runnable = () -> null;

    static {
        registMonitor();
    }

    public ResponseFuture(Request request) {
        super(runnable);

        this.request = request;
        FUTURES.put(this.request.uuid, this);
    }

    public static void receive(Response response) {

        ResponseFuture future = FUTURES.remove(response.uuid);

        if (future != null) {
            future.response = response;
            future.set(response);
        } else {
            logger.warn("The timeout response finally returned at "
                    + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()))
                    + ", response " + response
                    + (response.channel == null ? "" : ", channel: " + response.channel.localAddress()
                    + " -> " + response.channel.remoteAddress()));
        }
    }

    @Override
    @Deprecated
    public Response get() {
        throw new RuntimeException(" please call get(long timeout, TimeUnit unit)");
    }

    /**
     * 否则可能会有内存泄漏的风险，建议一个future不要在多线程中去get且超时还不一样。
     */
    @Override
    public Response get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        try {
            return (Response) super.get(timeout, unit);
        } catch (Exception e) {
            // 一个线程超时，其他线程也获取不到结果
            FUTURES.remove(request.uuid);
            throw e;
        }
    }

    @Override
    public void setException(Throwable t) {
        super.setException(t);
    }

    @Override
    public boolean cancel(boolean bool) {
        FUTURES.remove(request.uuid);
        return super.cancel(bool);
    }

    public static void registMonitor(){
        Monitor.regist(() -> {
            Monitor.value("pending_request_size", FUTURES.size());
        });
    }
}
//package pers.fq.hippo.transporter;
//
//import io.netty.channel.Channel;
//import pers.fq.hippo.common.SerialUtils;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
///**
// * @Description:
// * @author: fang
// * @date: Created by on 18/8/26
// */
//public class App {
//    public static void main(String[] args) throws Throwable {
//        String ip = "127.0.0.1";
//        int port = 8191;
//
//        ServerConfig config = new ServerConfig(new RequestHandler() {
//            @Override
//            public void handler(Channel channel, Request queryRequest) {
//                Response response = new Response(queryRequest.getUuid());
//                channel.writeAndFlush(SerialUtils.obj2Byte(response));
//            }
//        }, ip, port, 30_000);
//        NettyServer server = new NettyServer(config);
//
//        Map<Integer, String> map = new HashMap<>();
//        map.put(0, ip);
//        HippoClient client = new HippoClient(new ClientConfig(port, map));
//
//        Request request=  Request.getHeartbeatRequest();
//        Response response = (Response) client.send(request).get().get(100, TimeUnit.MILLISECONDS);
//
//        Thread.currentThread().join();
//    }
//}

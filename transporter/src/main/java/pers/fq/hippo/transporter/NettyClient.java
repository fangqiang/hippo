package pers.fq.hippo.transporter;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fq.hippo.common.SerialUtils;
import pers.fq.hippo.common.Utils;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * NettyServer
 */
public class NettyClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private Bootstrap bootstrap;

    private static EventLoopGroup workerGroup = new NioEventLoopGroup(Utils.STANDARD_THREAD_SIZE, new DefaultThreadFactory("NettyClient", true));

    private Channel channel;

    private final String remoteIp;
    private final int remotePort;

    NettyClient(String remoteIp, int remotePort) {
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
    }

    public void openAndConnect() {
        open();
        connect();
    }

    public void open() {

        bootstrap = new Bootstrap();

        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Config.MAX_BYTE, 0, 4, 0, 4));
                        ch.pipeline().addLast("bytesDecoder", new ByteArrayDecoder());
                        ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4));
                        ch.pipeline().addLast("bytesEncoder", new ByteArrayEncoder());
                        ch.pipeline().addLast("handler", new NettyClientHandler());
                    }
                });
    }

    public void connect() {

        // 第一次连接，创建新的channel
        ChannelFuture future = bootstrap.connect(remoteIp, remotePort);
        if(checkFuture(future)){

            // 赋值新的channel
            channel = future.channel();

            logger.info("successfully connect to {}:{}", remoteIp, remotePort);
        }else{
            // 客户端启动时，服务端就挂了
            logger.info("failed connect to {}:{}", remoteIp, remotePort);
        }
    }

    public void reconnect() {
        logger.warn("try reconnect");
        // 关闭旧连接
        close();
        // 重新连接
        connect();
    }

    public ResponseFuture send(Request request) {
        ResponseFuture responseFuture = new ResponseFuture(request);
        try {
            channel.writeAndFlush(request.toBytes()).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return responseFuture;
    }

    public boolean isActive(){
        return channel != null && channel.isActive();
    }

    public void close() {
        logger.info("close the connect from");
        try {
            if (channel != null) {
                // unbind.
                channel.close();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }

    private boolean checkFuture(ChannelFuture future){
        boolean isSucc = future.awaitUninterruptibly(3000, TimeUnit.MILLISECONDS);
        if(isSucc && future.isSuccess()) {
            logger.info("successful to connect the server, {}", remoteIp);
            return true;
        }else{
            logger.error("failed to connect to server {}:{} \n {} \n{}",
                    remoteIp,
                    remotePort,
                    future.cause() != null ? future.cause().getMessage() : null,
                    future.cause());
            return false;
        }
    }

    public SocketAddress remoteAddress(){
        return channel.remoteAddress();
    }

    public Channel getChannel(){
        return channel;
    }
}
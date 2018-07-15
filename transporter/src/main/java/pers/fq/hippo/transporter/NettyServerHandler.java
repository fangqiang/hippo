package pers.fq.hippo.transporter;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fq.hippo.common.SerialUtils;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 注意，chandler不是单例的可能会被实例化多次
 */
@io.netty.channel.ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private final RequestHandler handler;

    /**
     * 注意，chandler不是单例的可能会被实例化多次，每个链接初始化一次
     */
    public NettyServerHandler(RequestHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler == null");
        }

        this.handler = handler;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelRegistered: {}", ctx.channel().remoteAddress());
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelUnregistered: {}", ctx.channel().remoteAddress());
        ctx.fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelActive: {}", ctx.channel().remoteAddress());
        ctx.fireChannelActive();
        NettyServerChannelManager.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelInactive: {}", ctx.channel().remoteAddress());
        ctx.fireChannelInactive();
        NettyServerChannelManager.remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        Request request = Request.parseFrom((byte[])msg);

        logger.debug("receive msg: {}", request);

        if(request.isHeartbeat){
            logger.debug("heartbeat from client {}", ctx.channel().remoteAddress());

            NettyServerChannelManager.updateTime(ctx.channel());

            Response resp = new Response(request.getUuid());

            ctx.channel().writeAndFlush(resp.toBytes());
        }else {
            handler.handler(ctx.channel(), request);
        }
    }
}
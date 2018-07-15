package pers.fq.hippo.transporter;


import io.netty.channel.Channel;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/14
 */
public interface RequestHandler {
    void handler(Channel channel, Request queryRequest);
}

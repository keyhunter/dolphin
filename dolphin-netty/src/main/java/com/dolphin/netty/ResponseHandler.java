package com.dolphin.netty;

import com.dolphin.core.protocle.MessageReadListener;
import com.dolphin.core.protocle.ResponseListener;
import com.dolphin.core.protocle.transport.Message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ResponseHandler extends SimpleChannelInboundHandler<Message> {

    private static MessageReadListener listener = new ResponseListener();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        if (message.getHeader().isRequest()) {
            ctx.fireChannelRead(message);
            return;
        }
        listener.read(message);
        // 通知执行下一个InboundHandler
        ctx.fireChannelRead(message);
    }

}

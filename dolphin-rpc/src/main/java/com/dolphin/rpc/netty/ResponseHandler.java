package com.dolphin.rpc.netty;

import com.dolphin.rpc.core.io.MessageReadListener;
import com.dolphin.rpc.core.io.request.ResponseListener;
import com.dolphin.rpc.core.io.transport.Message;

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

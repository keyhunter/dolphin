package com.dolphin.netty.connector;

import com.dolphin.netty.HeartBeat;
import org.apache.log4j.Logger;

import com.dolphin.core.protocle.transport.Header;
import com.dolphin.core.protocle.transport.Message;
import com.dolphin.core.protocle.transport.PacketType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 空闲心跳检测服务
 * @author jiujie
 * @version $Id: IdleHeartBeatHandler.java, v 0.1 2016年7月5日 下午5:18:25 jiujie Exp $
 */
public class IdleHeartBeatHandler extends IdleStateHandler {

    public IdleHeartBeatHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds,
                                int allIdleTimeSeconds) {
        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
    }

    private static Logger logger = Logger.getLogger(IdleHeartBeatHandler.class.getName());

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (evt.state() == IdleState.WRITER_IDLE) {
            //发送心跳包
            if (logger.isDebugEnabled()) {
                logger.debug("Client heart beat.");
            }
            Message heartBeatMessage = new Message(new Header(PacketType.HEART_BEAT), new HeartBeat());
            ctx.writeAndFlush(heartBeatMessage);
        } else if (evt.state() == IdleState.READER_IDLE) {
            //读空闲，则关闭链路
            ctx.close();
        }
        super.channelIdle(ctx, evt);
    }

}

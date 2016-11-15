package com.dolphin.netty.connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import com.dolphin.netty.HeartBeat;
import org.apache.log4j.Logger;

import com.dolphin.core.protocle.transport.Header;
import com.dolphin.core.protocle.transport.Message;
import com.dolphin.core.protocle.transport.PacketType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;

/**
 * 定时心跳的NettyHandler，由于不是根据空闲时间来判断的，比较耗资源，不推荐使用
 * @author jiujie
 * @version $Id: HeartBeatConnectorHandler.java, v 0.1 2016年7月5日 下午5:17:21 jiujie Exp $
 */
@Deprecated
public class HeartBeatConnectorHandler extends SimpleChannelInboundHandler<Message> {

    private static Logger               logger = Logger
        .getLogger(HeartBeatConnectorHandler.class.getName());

    private volatile ScheduledFuture<?> heartBeat;

    public final byte[] input2byte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        EventExecutor executor = ctx.executor();
        heartBeat = executor.scheduleAtFixedRate(new HeartBeatTask(ctx),
            0, 5000, TimeUnit.MILLISECONDS);
        super.handlerAdded(ctx);
    }

    private class HeartBeatTask implements Runnable {

        private ChannelHandlerContext ctx;

        public HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            ctx.writeAndFlush(new Message(new Header(PacketType.HEART_BEAT), new HeartBeat()));
        }

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        if (message != null && message.getHeader() != null
            && message.getHeader().getPacketType() == PacketType.HEART_BEAT.getValue()) {
            logger.info("Client revieved server heart beat response success.");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        super.exceptionCaught(ctx, cause);
    }

}

package com.dolphin.netty;

import com.dolphin.core.protocle.AbstractConnection;

public class NettyConnection extends AbstractConnection {

    /** 与服务端的通道  @author jiujie 2016年5月13日 上午11:19:50 */
    private io.netty.channel.Channel channel;

    public NettyConnection(io.netty.channel.Channel channel) {
        this.channel = channel;
    }

    @Override
    public void write(Object object) {
        channel.write(object);
    }

    @Override
    public void writeAndFlush(Object object) {
        channel.writeAndFlush(object);
    }

    @Override
    public void doClose() {
        channel.close();
    }
}

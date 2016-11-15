package com.dolphin.netty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public abstract class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private Logger                            logger   = Logger
        .getLogger(NettyChannelInitializer.class);

    private List<Map<String, ChannelHandler>> handlers = new ArrayList();

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder", new NettyDecoder());
        pipeline.addLast("encoder", new NettyEncoder());
        registerHandler(ch);
        for (Map<String, ChannelHandler> handler : handlers) {
            for (Entry<String, ChannelHandler> entry : handler.entrySet()) {
                pipeline.addLast(entry.getKey(), entry.getValue());
            }
        }
    }

    public abstract void registerHandler(SocketChannel ch);

    /**
     * 注册共享的处理器
     * @author jiujie
     * 2016年5月25日 下午9:50:25
     * @param name
     * @param handler
     */
    public void registerHandler(String name, ChannelHandler handler) {
        if (StringUtils.isBlank(name) || handler == null) {
            return;
        }
        Map<String, ChannelHandler> handlerMap = new HashMap<>();
        handlerMap.put(name, handler);
        handlers.add(handlerMap);
        logger
            .info("Handler " + name + " (" + handler.getClass().getName() + ") has bean registed.");
    }

}

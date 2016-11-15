package com.dolphin.netty.server;

import org.apache.log4j.Logger;

import com.dolphin.netty.NettyChannelInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public abstract class NettyServer {

    private Logger                  logger                  = Logger.getLogger(NettyServer.class);

    private int                     port;

    private EventLoopGroup          bossGroup;
    private EventLoopGroup          workerGroup;

    /** 是否正在运行中  @author jiujie 2016年6月1日 下午1:20:44 */
    private volatile boolean        isStarting              = false;

    private NettyChannelInitializer nettyChannelInitializer = new NettyChannelInitializer() {

                                                                @Override
                                                                public void registerHandler(SocketChannel ch) {
                                                                }

                                                            };

    public NettyServer(int port) {
        this.port = port;
    }

    public void registerHandler(String name, ChannelHandler handler) {
        nettyChannelInitializer.registerHandler(name, handler);
    }

    public void shutdown() {
        synchronized (this) {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            isStarting = false;
        }
    }

    public void startup() {
        if (isStarting) {
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                shutdown();
            }
        }));
        synchronized (this) {
            if (isStarting) {
                return;
            }
            startServer();
            isStarting = true;
        }
    }

    private void startServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap(); // (2)
        ServerBootstrap serverBootstrap = b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class) // (3)
            .childHandler(nettyChannelInitializer);
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 128) // (5)
            .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
        // Bind and start to accept incoming connections.
        try {
            b.bind(port).sync();
        } catch (Exception e) {
            logger.error("", e);
            shutdown();
        }
    }

    public boolean isStarting() {
        return isStarting;
    }

}

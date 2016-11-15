package com.dolphin.netty.connector;

import java.util.concurrent.atomic.AtomicBoolean;

import com.dolphin.netty.NettyChannelInitializer;
import com.dolphin.netty.NettyConnection;
import com.dolphin.netty.ResponseHandler;
import org.apache.log4j.Logger;

import com.dolphin.core.exception.AddressFormatException;
import com.dolphin.core.protocle.Connection;
import com.dolphin.core.protocle.ConnectionManager;
import com.dolphin.core.protocle.Connector;
import com.dolphin.core.protocle.HostAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyConnector implements Connector {

    private Logger                  logger                  = Logger
        .getLogger(NettyConnector.class);

    /** 是否正在运行中  @author jiujie 2016年6月1日 下午1:20:44 */
    private volatile AtomicBoolean  isStarting              = new AtomicBoolean(false);

    private ConnectionManager       connectionManager       = ConnectionManager.getInstance();

    private NettyChannelInitializer nettyChannelInitializer = new NettyChannelInitializer() {

                                                                @Override
                                                                public void registerHandler(SocketChannel ch) {
                                                                    ch.pipeline().addLast("timeout",
                                                                        new IdleHeartBeatHandler(3,
                                                                            1, 0));
                                                                }

                                                            };

    public NettyConnector() {
        super();
        registerHandler("responseHandler", new ResponseHandler());
    }

    public void registerHandler(String name, ChannelHandler handler) {
        nettyChannelInitializer.registerHandler(name, handler);
    }

    public static void main(String[] args) {
        NettyConnector nettyClient = new NettyConnector();
        nettyClient.connect(new HostAddress("10.1.1.31", 1114));
    }

    @Override
    public void shutdown() {
        synchronized (this) {
            free();
        }
    }

    private void free() {
        isStarting.set(false);
    }

    @Override
    public Connection connect(final HostAddress address) {
        if (!HostAddress.verify(address)) {
            throw new AddressFormatException();
        }

        //客户端启动中---------
        synchronized (this) {
            return startConnect(address);
        }
    }

    private Connection startConnect(final HostAddress address) {
        final NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class)
            .handler(nettyChannelInitializer);
        ChannelFuture future = bootstrap.connect(address.getHost(), address.getPort());
        // awaitUninterruptibly() 等待连接成功
        io.netty.channel.Channel channel = future.awaitUninterruptibly().channel();
        final Connection connection = connectionManager.create(new NettyConnection(channel));
        channel.closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> arg0) throws Exception {
                logger.error("Connection closed: [" + connection.getId() + "]");
                try {
                    connection.close();
                    connectionManager.remove(connection.getId());
                } finally {
                    //连接关闭，释放资源
                    group.shutdownGracefully();
                }
            }
        });
        return connection;
    }

    @Override
    public void startup() {
        //初始化客端
        //TODO 初始化最大连接数
        //初始化一个清理线程，来清理已经关闭Connection
        //是否启动重连服务，启动重连服务的话，如果断开的话，就定时重连，不启动重连服务的话，就不再重连

    }

}

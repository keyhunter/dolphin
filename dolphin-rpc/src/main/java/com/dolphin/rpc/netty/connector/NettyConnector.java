package com.dolphin.rpc.netty.connector;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.dolphin.rpc.core.exception.AddressFormatException;
import com.dolphin.rpc.core.io.Connection;
import com.dolphin.rpc.core.io.ConnectionCloseListenser;
import com.dolphin.rpc.core.io.ConnectionManager;
import com.dolphin.rpc.core.io.Connector;
import com.dolphin.rpc.core.io.HostAddress;
import com.dolphin.rpc.netty.NettyConnection;
import com.dolphin.rpc.netty.NettyChannelInitializer;
import com.dolphin.rpc.netty.ResponseHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyConnector implements Connector, ConnectionCloseListenser {

    private Logger                   logger                  = Logger
        .getLogger(NettyConnector.class);

    private HostAddress              hostAddress;

    /** 是否正在运行中  @author jiujie 2016年6月1日 下午1:20:44 */
    private volatile AtomicBoolean   isStarting              = new AtomicBoolean(false);

    private ConnectionManager        connectionManager       = ConnectionManager.getInstance();

    private ScheduledExecutorService executor;

    private NettyChannelInitializer  nettyChannelInitializer = new NettyChannelInitializer();

    private EventLoopGroup           group;

    public NettyConnector() {
        super();
        nettyChannelInitializer.registerHandler("responseHandler", new ResponseHandler());

    }

    public void registerHandler(String name, ChannelHandler handler) {
        nettyChannelInitializer.registerHandler(name, handler);
    }

    public static void main(String[] args) {
        NettyConnector nettyClient = new NettyConnector();
        nettyClient.connect(new HostAddress("10.1.1.31", 1114));
    }

    public HostAddress getHostAddress() {
        return hostAddress;
    }

    @Override
    public void shutdown() {
        synchronized (this) {
            free();
        }
    }

    private void free() {
        group.shutdownGracefully();
        executor.shutdown();
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
        if (group == null || group.isShutdown()) {
            group = new NioEventLoopGroup();
            executor = Executors.newSingleThreadScheduledExecutor();
        }
        this.hostAddress = address;
        Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class)
            .handler(nettyChannelInitializer);
        ChannelFuture future = bootstrap.connect(hostAddress.getHost(), hostAddress.getPort());
        // awaitUninterruptibly() 等待连接成功
        io.netty.channel.Channel channel = future.awaitUninterruptibly().channel();
        final Connection connection = connectionManager.create(new NettyConnection(channel));
        connection.addCloseListener(this);
        future.channel().closeFuture()
            .addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> arg0) throws Exception {
                    connection.close();
                }
            });
        return connection;
    }

    @Override
    public void startup() {
        //初始化客端
        //初始化最大连接数
        //初始化一个清理线程，来清理已经关闭Connection
        //是否启动重连服务，启动重连服务的话，如果断开的话，就定时重连，不启动重连服务的话，就不再重连
    }

    @Override
    public void close(Connection channel) {
        connectionManager.remove(channel.getId());
    }

}

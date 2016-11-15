package com.dolphin.test;

import com.dolphin.core.protocle.Connection;
import com.dolphin.core.protocle.HostAddress;
import com.dolphin.netty.connector.NettyConnector;
import com.dolphin.netty.server.NettyServer;

import io.netty.channel.ChannelHandler;

public class AllTest {

    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer(1991) {
            @Override
            public void registerHandler(String name, ChannelHandler handler) {
                super.registerHandler(name, handler);
            }
        };
        NettyConnector nettyConnector = new NettyConnector();
        Connection connect = nettyConnector.connect(new HostAddress("10.1.1.31", 1991));
        System.out.println(connect.getId());
    }

}

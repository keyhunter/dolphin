package com.dolphin.rpc.test;

import com.dolphin.rpc.core.io.Connection;
import com.dolphin.rpc.core.io.HostAddress;
import com.dolphin.rpc.netty.connector.NettyConnector;
import com.dolphin.rpc.netty.server.NettyServer;
import com.dolphin.rpc.server.RPCServer;

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

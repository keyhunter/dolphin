package com.dolphin.rpc.registry.netty;

import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.dolphin.rpc.core.ApplicationType;
import com.dolphin.rpc.core.exception.RPCRunTimeException;
import com.dolphin.rpc.core.io.Connection;
import com.dolphin.rpc.core.io.ConnectionCloseListenser;
import com.dolphin.rpc.core.io.HostAddress;
import com.dolphin.rpc.core.io.transport.Header;
import com.dolphin.rpc.core.io.transport.Message;
import com.dolphin.rpc.core.io.transport.PacketType;
import com.dolphin.rpc.netty.connector.NettyConnector;
import com.dolphin.rpc.registry.MySQLRegistryAddressContainer;
import com.dolphin.rpc.registry.ServiceInfo;
import com.dolphin.rpc.registry.netty.protocle.Commands;
import com.dolphin.rpc.registry.netty.protocle.RegistryRequest;
import com.dolphin.rpc.registry.provider.AbstractServiceProvider;
import com.dolphin.rpc.registry.provider.ServiceProvider;

public class NettyServiceProvider extends AbstractServiceProvider implements ServiceProvider {

    private Logger         logger = Logger.getLogger(NettyServiceProvider.class);

    private Connection     connection;

    private NettyConnector nettyConnector;

    public NettyServiceProvider(ServiceInfo serviceInfo) {
        super(serviceInfo);
        nettyConnector = new NettyConnector();
        connectToRegistryServer();
        // 要起个线程，自动重连，防止注册中心挂了
        connection.addCloseListener(new ConnectionCloseListenser() {
            @Override
            public void close(Connection connection) {
                connectToRegistryServer();
            }
        });
        registerSelf();
    }

    /**
     * 连接到注册中心
     * @author jiujie
     * 2016年6月29日 下午12:48:28
     */
    private void connectToRegistryServer() {
        //注册中心地址 
        List<HostAddress> all = MySQLRegistryAddressContainer.getInstance().getAll();
        if (all == null || all.isEmpty()) {
            throw new RPCRunTimeException("There is no registry server.");
        }
        HostAddress masterRegistryAddress = all.get(new Random().nextInt(all.size()));
        logger.info("Connecting registry server [" + masterRegistryAddress.toString() + "]");
        connection = nettyConnector.connect(masterRegistryAddress);
    }

    @Override
    public void register(ServiceInfo serviceInfo) {
        //TODO 获取自己的地址去注册
        RegistryRequest registryRequest = new RegistryRequest(ApplicationType.RPC_SERVER,
            Commands.REGISTER, serviceInfo);
        connection.writeAndFlush(new Message(new Header(PacketType.REGISTRY), registryRequest));
    }

    @Override
    public void unRegister(ServiceInfo serviceInfo) {
        RegistryRequest registryRequest = new RegistryRequest(ApplicationType.RPC_SERVER,
            Commands.UN_REGISTER, serviceInfo);
        connection.writeAndFlush(new Message(new Header(PacketType.REGISTRY), registryRequest));
    }

}

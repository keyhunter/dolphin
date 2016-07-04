package com.dolphin.rpc.registry.netty.server;

import java.util.ArrayList;
import java.util.List;

import com.dolphin.rpc.core.ApplicationType;
import com.dolphin.rpc.core.io.transport.Message;
import com.dolphin.rpc.netty.connector.NettyConnector;
import com.dolphin.rpc.registry.ServiceInfo;
import com.dolphin.rpc.registry.netty.protocle.Commands;
import com.dolphin.rpc.registry.netty.protocle.RegistryResponse;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 注册中心服务连接其他注册中心的连接器
 * @author jiujie
 * @version $Id: RegistryConnector.java, v 0.1 2016年6月1日 下午7:11:06 jiujie Exp $
 */
public class RegistryConnector extends NettyConnector {

    private List<ServiceRegisterListener> registryServerInfoListeners = new ArrayList<>();

    public RegistryConnector(ServiceRegisterListener... listeners) {
        registerHandler("registryHandler", new RegstryHandler());
        if (listeners != null) {
            for (ServiceRegisterListener listener : listeners) {
                registryServerInfoListeners.add(listener);
            }
        }
    }

    @Sharable
    public class RegstryHandler extends SimpleChannelInboundHandler<Message> {
        @Override
        protected void channelRead0(ChannelHandlerContext arg0, Message arg1) throws Exception {
            RegistryResponse response = arg1.getBody(RegistryResponse.class);
            switch (response.getCommand()) {
                case Commands.REGISTER: {
                    ServiceInfo serviceInfo = (ServiceInfo) response.getResult();
                    for (ServiceRegisterListener listener : registryServerInfoListeners) {
                        listener.register(ApplicationType.REGISTRY_SERVER, serviceInfo);
                    }
                    break;
                }
                case Commands.UN_REGISTER: {
                    ServiceInfo serviceInfo = (ServiceInfo) response.getResult();
                    for (ServiceRegisterListener listener : registryServerInfoListeners) {
                        listener.unRegister(ApplicationType.REGISTRY_SERVER, serviceInfo);
                    }
                    break;
                }
                default:
                    break;
            }
        }

    }

}

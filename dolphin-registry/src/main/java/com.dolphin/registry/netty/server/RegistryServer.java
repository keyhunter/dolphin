package com.dolphin.registry.netty.server;

import java.io.IOException;
import java.util.List;

import com.dolphin.core.protocle.transport.*;
import org.apache.log4j.Logger;

import com.dolphin.core.ApplicationType;
import com.dolphin.core.protocle.Connection;
import com.dolphin.core.protocle.ConnectionManager;
import com.dolphin.core.protocle.HostAddress;
import com.dolphin.core.protocle.Server;
import com.dolphin.netty.NettyConnection;
import com.dolphin.netty.server.NettyServer;
import com.dolphin.registry.MySQLRegistryAddressContainer;
import com.dolphin.registry.RegistryAddressContainer;
import com.dolphin.registry.netty.protocle.Commands;
import com.dolphin.registry.netty.protocle.RegistryRequest;
import com.dolphin.registry.netty.protocle.RegistryResponse;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class RegistryServer extends NettyServer implements Server {

    private RegistryAddressContainer registryAddressContainer = MySQLRegistryAddressContainer
        .getInstance();

    private ServiceManager           serviceManager           = ServiceManager.getInstance();

    private HostAddress              hostAddress;

    private static final String      REGISTER_SERVICE_KEY     = "registerService";
    private static final String      REGISTRY_SERVER_KEY      = "registryServer";

    public RegistryServer(int port) {
        super(port);
        this.hostAddress = new HostAddress("10.1.1.31", port);
        registerHandler("registryHandler", new RegistryServerHandler());
    }

    @Override
    public void startup() {
        super.startup();
        serviceManager.registerSelf(hostAddress);
        //sycn data
        serviceManager.sycnServiceInfoContainer();
        registryAddressContainer.add(hostAddress);
    }

    @Override
    public void shutdown() {
        //TODO notify all connector this will be removed
        if (isStarting()) {
            registryAddressContainer.remove(hostAddress);
        }
        super.shutdown();
    }

    public static void main(String[] args) {
        RegistryServer registryServer = new RegistryServer(9092);
        registryServer.startup();
        System.out.println("press ENTER to call System.exit() and run the shutdown routine.");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * RPC方法调用的Handler，用于解析RPC请求并处理请求的
     * @author jiujie
     * @version $Id: RPCServer.java, v 0.1 2016年5月23日 下午2:39:58 jiujie Exp $
     */
    @Sharable
    private static class RegistryServerHandler extends SimpleChannelInboundHandler<Message> {

        private static Logger                   logger            = Logger
            .getLogger(RegistryServerHandler.class.getName());

        private static final AttributeKey<Long> CONNECTION_ID_KEY = AttributeKey
            .newInstance("connId");

        private ConnectionManager               connectionManager = ConnectionManager.getInstance();

        private ServiceManager                  serviceManager    = ServiceManager.getInstance();

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            super.handlerAdded(ctx);
            Connection connection = connectionManager.create(new NettyConnection(ctx.channel()));
            ctx.attr(CONNECTION_ID_KEY).set(connection.getId());
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            super.handlerRemoved(ctx);
            Long connId = ctx.attr(CONNECTION_ID_KEY).get();
            Connection connection = connectionManager.get(connId);
            ServiceInfo serviceInfo = (ServiceInfo) connection.getAttribute(REGISTER_SERVICE_KEY);
            if (serviceInfo != null) {
                serviceManager.unRegister(ApplicationType.RPC_SERVER, serviceInfo);
            }
            serviceInfo = (ServiceInfo) connection.getAttribute(REGISTRY_SERVER_KEY);
            if (serviceInfo != null) {
                serviceManager.unRegisterRegistryServer(connection.getId(), serviceInfo);
            }
        };

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

            Header header = message.getHeader();
            if (header != null && header.getPacketType() == PacketType.HEART_BEAT.getValue()) {
                RPCRequest requset = message.getBody(RPCRequest.class);
                RPCResult result = new RPCResult();
                result.setRequestId(requset.getId());
                ctx.writeAndFlush(new Message(message.getHeader(), result));
                return;
            }
            Long connId = ctx.attr(CONNECTION_ID_KEY).get();
            Connection connection = connectionManager.get(connId);
            RegistryRequest registryRequest = message.getBody(RegistryRequest.class);
            if (header != null && header.getPacketType() == PacketType.REGISTRY.getValue()) {
                ServiceInfo serviceInfo = registryRequest.getServiceInfo();
                logger.info("RequestId [" + registryRequest.getId() + "] "
                            + registryRequest.getCommand() + " [" + serviceInfo + "]");
                switch (registryRequest.getCommand()) {
                    case Commands.GET_SERVICES: {
                        //根据client的ID分配Service
                        ServiceInfo[] services = serviceManager.getServices(serviceInfo.getGroup(),
                            serviceInfo.getName());
                        RegistryResponse response = new RegistryResponse();
                        response.setRequestId(registryRequest.getId());
                        response.setResult(services);
                        ctx.writeAndFlush(new Message(message.getHeader(), response));
                        break;
                    }
                    case Commands.SUBCRIBE:
                        serviceManager.subcribe(connection.getId(), serviceInfo.getGroup(),
                            serviceInfo.getName());
                        break;
                    case Commands.UN_SUBCRIBE:
                        serviceManager.unSubcribe(connection.getId(), serviceInfo.getGroup(),
                            serviceInfo.getName());
                        break;
                    case Commands.REGISTER:
                        serviceManager.register(registryRequest.getApplicationType(), serviceInfo);
                        connection.setAttribute(REGISTER_SERVICE_KEY, serviceInfo);
                        break;
                    case Commands.UN_REGISTER:
                        serviceManager.unRegister(registryRequest.getApplicationType(),
                            serviceInfo);
                        connection.removeAttribute(REGISTER_SERVICE_KEY);
                        break;
                    case Commands.REGISTER_REGISTRY_SERVER: {
                        serviceManager.registerRegistryServer(connId, serviceInfo);
                        connection.setAttribute(REGISTRY_SERVER_KEY, serviceInfo);
                        break;
                    }
                    case Commands.UN_REGISTER_REGISTRY_SERVER: {
                        serviceManager.unRegisterRegistryServer(connId, serviceInfo);
                        connection.removeAttribute(REGISTRY_SERVER_KEY);
                        break;
                    }
                    case Commands.SYCN_SERVICE_INFO: {
                        List<ServiceInfo> serviceInfos = serviceManager.getServiceInfos();
                        RegistryResponse response = new RegistryResponse(Commands.SYCN_SERVICE_INFO,
                            serviceInfos);
                        response.setRequestId(registryRequest.getId());
                        ctx.writeAndFlush(new Message(message.getHeader(), response));
                        break;
                    }
                    default:
                        break;
                }
            }
        }

    }

}

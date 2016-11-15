package com.dolphin.registry.netty;

import java.util.List;
import java.util.Random;

import com.dolphin.core.protocle.transport.ServiceInfo;
import org.apache.log4j.Logger;

import com.dolphin.core.ApplicationType;
import com.dolphin.core.exception.RPCException;
import com.dolphin.core.protocle.Connection;
import com.dolphin.core.protocle.ConnectionCloseListenser;
import com.dolphin.core.protocle.HostAddress;
import com.dolphin.core.protocle.request.RequestManager;
import com.dolphin.core.protocle.transport.Header;
import com.dolphin.core.protocle.transport.Message;
import com.dolphin.core.protocle.transport.PacketType;
import com.dolphin.netty.connector.NettyConnector;
import com.dolphin.registry.MySQLRegistryAddressContainer;
import com.dolphin.registry.consumer.AbstractServiceCustomer;
import com.dolphin.registry.netty.protocle.Commands;
import com.dolphin.registry.netty.protocle.RegistryRequest;
import com.dolphin.registry.netty.protocle.RegistryResponse;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Netty服务的消费者，订阅和得到ServiceInfo
 * @author jiujie
 * @version $Id: NettyServiceCustomer.java, v 0.1 2016年5月25日 下午10:31:46 jiujie Exp $
 */
public class NettyServiceConsumer extends AbstractServiceCustomer {

    private RequestManager requestManager = RequestManager.getInstance();

    private Logger         logger         = Logger.getLogger(NettyServiceConsumer.class);

    private NettyConnector nettyConnector;

    private Connection     connection;

    public NettyServiceConsumer() {
        super();
        //启动连接器
        nettyConnector = new NettyConnector();
        // 连接注册中心地址
        connectToRegistryServer();
    }

    /**
     * 连接到注册中心
     * @author jiujie
     * 2016年6月29日 下午12:48:28
     */
    private void connectToRegistryServer() {
        //注册中心地址 
        List<HostAddress> all = MySQLRegistryAddressContainer.getInstance().getAll();
        HostAddress masterRegistryAddress = all.get(new Random().nextInt(all.size()));
        logger.info("Connecting registry server [" + masterRegistryAddress.toString() + "]");
        connection = nettyConnector.connect(masterRegistryAddress);
        //要起个线程，自动重连，防止注册中心挂了
        connection.addCloseListener(new ConnectionCloseListenser() {
            @Override
            public void close(Connection connection) {
                connectToRegistryServer();
            }
        });
        nettyConnector.registerHandler("notifyHandler", new NotifyHandler());
    }

    @Sharable
    public class NotifyHandler extends SimpleChannelInboundHandler<Message> {
        @Override
        protected void channelRead0(ChannelHandlerContext arg0, Message arg1) throws Exception {
            RegistryRequest request = arg1.getBody(RegistryRequest.class);
            switch (request.getCommand()) {
                case Commands.REGISTER: {
                    ServiceInfo serviceInfo = request.getServiceInfo();
                    change(serviceInfo.getGroup(), serviceInfo.getName());
                    break;
                }
                case Commands.UN_REGISTER: {
                    ServiceInfo serviceInfo = request.getServiceInfo();
                    change(serviceInfo.getGroup(), serviceInfo.getName());
                    break;
                }
                default:
                    break;
            }
        }

    }

    @Override
    public ServiceInfo[] getRemoteServiceInfos(String group, String serviceName) {
        RegistryRequest registryRequest = new RegistryRequest(ApplicationType.RPC_CLIENT,
            Commands.GET_SERVICES, new ServiceInfo(group, serviceName, null));
        RegistryResponse response = null;
        try {
            response = (RegistryResponse) requestManager.sysnRequest(connection,
                new Header(PacketType.REGISTRY), registryRequest);
        } catch (RPCException e) {
            logger.error("", e);
            return null;
        }
        return (ServiceInfo[]) response.getResult();
    }

    @Override
    public void subcride(String group, String serviceName) {
        RegistryRequest registryRequest = new RegistryRequest(ApplicationType.RPC_CLIENT,
            Commands.SUBCRIBE, new ServiceInfo(group, serviceName, null));
        connection.writeAndFlush(new Message(new Header(PacketType.REGISTRY), registryRequest));
    }

    @Override
    public void unSubcride(String group, String serviceName) {
        RegistryRequest registryRequest = new RegistryRequest(ApplicationType.RPC_CLIENT,
            Commands.UN_SUBCRIBE, new ServiceInfo(group, serviceName, null));
        connection.writeAndFlush(new Message(new Header(PacketType.REGISTRY), registryRequest));
    }

}

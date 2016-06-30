package com.dolphin.rpc.registry.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.dolphin.rpc.core.ApplicationType;
import com.dolphin.rpc.core.exception.ServiceInfoFormatException;
import com.dolphin.rpc.core.io.Connection;
import com.dolphin.rpc.core.io.ConnectionCloseListenser;
import com.dolphin.rpc.core.io.HostAddress;
import com.dolphin.rpc.core.io.request.RequestManager;
import com.dolphin.rpc.core.io.transport.Header;
import com.dolphin.rpc.core.io.transport.Message;
import com.dolphin.rpc.core.io.transport.PacketType;
import com.dolphin.rpc.netty.connector.NettyConnector;
import com.dolphin.rpc.registry.MySQLRegistryAddressContainer;
import com.dolphin.rpc.registry.ServiceInfo;
import com.dolphin.rpc.registry.ServiceInfoContainer;
import com.dolphin.rpc.registry.ServiceInfoContainer.ServiceInfoSet;
import com.dolphin.rpc.registry.ServiceListener;
import com.dolphin.rpc.registry.protocle.Commands;
import com.dolphin.rpc.registry.protocle.RegistryRequest;
import com.dolphin.rpc.registry.protocle.RegistryResponse;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Netty服务的消费者，订阅和得到ServiceInfo
 * @author jiujie
 * @version $Id: NettyServiceCustomer.java, v 0.1 2016年5月25日 下午10:31:46 jiujie Exp $
 */
public class NettyServiceConsumer extends NettyConnector implements ServiceCustomer {

    private RequestManager        requestManager        = RequestManager.getInstance();

    private ServiceInfoContainer  cachedServiceInfos    = new ServiceInfoContainer();

    private Logger                logger                = Logger
        .getLogger(NettyServiceConsumer.class);

    private List<ServiceListener> listeners             = new ArrayList<>();

    private Connection            connection;

    public NettyServiceConsumer() {
        super();
        // 连接注册中心地址
        connectToRegistryServer();
        //要起个线程，自动重连，防止注册中心挂了
        connection.addCloseListener(new ConnectionCloseListenser() {
            @Override
            public void close(Connection connection) {
                connectToRegistryServer();
            }
        });
        registerHandler("notifyHandler", new NotifyHandler());
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
        connection = connect(masterRegistryAddress);
    }

    @Sharable
    public class NotifyHandler extends SimpleChannelInboundHandler<Message> {
        @Override
        protected void channelRead0(ChannelHandlerContext arg0, Message arg1) throws Exception {
            RegistryRequest request = arg1.getBody(RegistryRequest.class);
            switch (request.getCommand()) {
                case Commands.REGISTER: {
                    ServiceInfo serviceInfo = request.getServiceInfo();
                    register(serviceInfo);
                    break;
                }
                case Commands.UN_REGISTER: {
                    ServiceInfo serviceInfo = request.getServiceInfo();
                    unRegister(serviceInfo);
                    break;
                }
                default:
                    break;
            }
        }

    }

    /**
     * 添加Serive改变监听器
     * @author jiujie
     * 2016年5月25日 下午10:11:35
     * @param listener
     */
    public void addServiceListener(ServiceListener listener) {
        if (listener == null) {
            return;
        }
        listeners.add(listener);
    }

    @Override
    public ServiceInfo[] getServices(String group, String serviceName) {
        ServiceInfoSet serviceInfoSet = cachedServiceInfos.get(group, serviceName);
        if (serviceInfoSet != null && serviceInfoSet.size() > 0) {
            return (ServiceInfo[]) serviceInfoSet.toArray();
        }
        ServiceInfo[] serviceInfos = getRemoteServiceInfos(group, serviceName);
        if (serviceInfos != null && serviceInfos.length > 0) {
            for (ServiceInfo serviceInfo : serviceInfos) {
                cachedServiceInfos.add(serviceInfo);
            }
        }
        return serviceInfos;
    }

    private ServiceInfo[] getRemoteServiceInfos(String group, String serviceName) {
        RegistryRequest registryRequest = new RegistryRequest(ApplicationType.RPC_CLIENT,
            Commands.GET_SERVICES, new ServiceInfo(group, serviceName, null));
        RegistryResponse response = (RegistryResponse) requestManager.sysnRequest(connection,
            new Header(PacketType.REGISTRY), registryRequest);
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

    @Override
    public void register(ServiceInfo serviceInfo) {
        if (serviceInfo == null || serviceInfo.getHostAddress() == null
            || HostAddress.verify(serviceInfo.getHostAddress())) {
            throw new ServiceInfoFormatException();
        }
        cachedServiceInfos.add(serviceInfo);
        for (ServiceListener listener : listeners) {
            listener.register(serviceInfo);
        }
    }

    @Override
    public void unRegister(ServiceInfo serviceInfo) {
        if (serviceInfo == null || serviceInfo.getHostAddress() == null
            || HostAddress.verify(serviceInfo.getHostAddress())) {
            throw new ServiceInfoFormatException();
        }
        cachedServiceInfos.remove(serviceInfo);
        for (ServiceListener listener : listeners) {
            listener.unRegister(serviceInfo);
        }
    }

}

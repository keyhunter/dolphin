package com.dolphin.registry.netty.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.dolphin.core.protocle.transport.ServiceInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dolphin.core.ApplicationType;
import com.dolphin.core.exception.RPCException;
import com.dolphin.core.protocle.Connection;
import com.dolphin.core.protocle.ConnectionManager;
import com.dolphin.core.protocle.HostAddress;
import com.dolphin.core.protocle.Response;
import com.dolphin.core.protocle.request.RequestManager;
import com.dolphin.core.protocle.transport.Header;
import com.dolphin.core.protocle.transport.Message;
import com.dolphin.core.protocle.transport.PacketType;
import com.dolphin.registry.MySQLRegistryAddressContainer;
import com.dolphin.registry.RegistryAddressContainer;
import com.dolphin.registry.ServiceInfoContainer;
import com.dolphin.registry.ServiceInfoContainer.ServiceInfoSet;
import com.dolphin.registry.netty.protocle.Commands;
import com.dolphin.registry.netty.protocle.RegistryRequest;
import com.dolphin.registry.netty.protocle.RegistryResponse;

/**
 * Service的管理器，用于管理服务
 * @author jiujie
 * @version $Id: ServiceManager.java, v 0.1 2016年5月16日 下午8:28:48 jiujie Exp $
 */
public class ServiceManager implements ServiceRegisterListener {

    private ServiceInfoContainer                serviceInfoContainer               = new ServiceInfoContainer();

    private Map<String, Map<String, Set<Long>>> notifyClients                      = new ConcurrentHashMap<>();

    /** 连接到这台注册中心的连接ID @author jiujie 2016年6月6日 下午2:03:32 */
    private Set<Long>                           connectToThisRegistryServerConnIds = new HashSet<>();

    private static ServiceManager               serviceManager;

    private List<Connection>                    registryConnections;

    private RegistryAddressContainer            registryAddressContainer           = MySQLRegistryAddressContainer
        .getInstance();

    private RegistryConnector                   registryConnector;

    private Logger                              logger                             = LoggerFactory
        .getLogger(ServiceManager.class);

    private ServiceManager() {
        //connect to other ServiceRegistryServers
        registryConnections = new ArrayList<>();
        List<HostAddress> all = registryAddressContainer.getAll();
        registryConnector = new RegistryConnector(this);
        if (all != null && !all.isEmpty()) {
            for (HostAddress address : all) {
                registryConnections.add(registryConnector.connect(address));
            }
        }
    }

    public static ServiceManager getInstance() {
        if (serviceManager == null) {
            synchronized (ServiceManager.class) {
                if (serviceManager == null) {
                    serviceManager = new ServiceManager();
                }
            }
        }
        return serviceManager;
    }

    public void registerSelf(HostAddress address) {
        ServiceInfo self = new ServiceInfo(null, null, address);

        //1. notify RegistryConnectors
        if (registryConnections != null) {
            RegistryRequest registryRequest = new RegistryRequest(ApplicationType.REGISTRY_SERVER,
                Commands.REGISTER_REGISTRY_SERVER, self);
            Message message = new Message(new Header(PacketType.REGISTRY), registryRequest);
            for (Connection connection : registryConnections) {
                connection.writeAndFlush(message);
            }
        }
        //2. notify connected RegistryServers
        if (connectToThisRegistryServerConnIds != null) {
            RegistryResponse registryResponse = new RegistryResponse(
                Commands.REGISTER_REGISTRY_SERVER, self);
            Message message = new Message(new Header(PacketType.REGISTRY), registryResponse);
            for (long notifyRegistryServerConnId : connectToThisRegistryServerConnIds) {
                Connection connection = ConnectionManager.getInstance()
                    .get(notifyRegistryServerConnId);
                if (connection == null) {
                    connectToThisRegistryServerConnIds.remove(notifyRegistryServerConnId);
                }
                connection.writeAndFlush(message);
            }
        }
    }

    public void unRegisterSelf(HostAddress address) {
        ServiceInfo self = new ServiceInfo(null, null, address);

        //1. notify RegistryConnectors
        if (registryConnections != null) {
            RegistryRequest registryRequest = new RegistryRequest(ApplicationType.REGISTRY_SERVER,
                Commands.UN_REGISTER_REGISTRY_SERVER, self);
            Message message = new Message(new Header(PacketType.REGISTRY), registryRequest);
            Iterator<Connection> iterator = registryConnections.iterator();
            while (iterator.hasNext()) {
                Connection connection = iterator.next();
                connection.writeAndFlush(message);
            }
            registryConnections.clear();
        }
        //2. notify connected RegistryServers
        if (connectToThisRegistryServerConnIds != null) {
            RegistryResponse registryResponse = new RegistryResponse(
                Commands.UN_REGISTER_REGISTRY_SERVER, self);
            Message message = new Message(new Header(PacketType.REGISTRY), registryResponse);
            for (long notifyRegistryServerConnId : connectToThisRegistryServerConnIds) {
                Connection connection = ConnectionManager.getInstance()
                    .get(notifyRegistryServerConnId);
                if (connection == null) {
                    connectToThisRegistryServerConnIds.remove(notifyRegistryServerConnId);
                }
                connection.writeAndFlush(message);
            }
        }
    }

    /**
     * @author jiujie
     * 2016年6月1日 下午7:56:59
     * @param serviceInfo
     */
    public void registerRegistryServer(long connId, ServiceInfo serviceInfo) {
        connectToThisRegistryServerConnIds.add(connId);
    }

    /**
     * @author jiujie
     * 2016年6月1日 下午7:56:59
     * @param serviceInfo
     */
    public void unRegisterRegistryServer(long connId, ServiceInfo serviceInfo) {
        connectToThisRegistryServerConnIds.remove(connId);
    }

    /**
     * 订阅Service
     * @author jiujie
     * 2016年5月24日 下午9:13:28
     * @param connId
     * @param group
     * @param serviceName
     */
    public void subcribe(long connId, String group, String serviceName) {
        if (StringUtils.isBlank(group) || StringUtils.isBlank(serviceName)) {
            return;
        }
        Set<Long> connIds = getConnIdSet(group, serviceName);
        connIds.add(connId);
    }

    public void unSubcribe(long connId, String group, String serviceName) {
        if (StringUtils.isBlank(group) || StringUtils.isBlank(serviceName)) {
            return;
        }
        Set<Long> connIds = getConnIdSet(group, serviceName);
        connIds.remove(connId);
    }

    private Set<Long> getConnIdSet(String group, String serviceName) {
        Map<String, Set<Long>> groupConnIds = notifyClients.get(group);
        Set<Long> connIds = null;
        if (groupConnIds == null) {
            synchronized (notifyClients) {
                if (groupConnIds == null) {
                    groupConnIds = new HashMap<>();
                    connIds = new HashSet<>();
                    groupConnIds.put(serviceName, connIds);
                    notifyClients.put(group, groupConnIds);
                }
            }
        } else {
            connIds = groupConnIds.get(serviceName);
        }
        return connIds;
    }

    public void register(ApplicationType applicationType, ServiceInfo serviceInfo) {
        if (serviceInfo == null || StringUtils.isBlank(serviceInfo.getName())
            || serviceInfo.getHostAddress() == null) {
            return;
        }
        Set<ServiceInfo> serviceInfoSet = serviceInfoContainer.get(serviceInfo.getGroup(),
            serviceInfo.getName());
        serviceInfoSet.add(serviceInfo);
        // notify register
        notifyRpcConnectorRegister(serviceInfo);
        if (applicationType.getValue() != ApplicationType.REGISTRY_SERVER.getValue()) {
            //1. notify RegistryConnectors
            if (registryConnections != null) {
                for (Connection connection : registryConnections) {
                    RegistryRequest registryRequest = new RegistryRequest(
                        ApplicationType.REGISTRY_SERVER, Commands.REGISTER, serviceInfo);
                    connection.writeAndFlush(
                        new Message(new Header(PacketType.REGISTRY), registryRequest));
                }
            }
            //2. notify connected RegistryServers
            if (connectToThisRegistryServerConnIds != null) {
                for (long notifyRegistryServerConnId : connectToThisRegistryServerConnIds) {
                    RegistryResponse registryResponse = new RegistryResponse(Commands.REGISTER,
                        serviceInfo);
                    Connection connection = ConnectionManager.getInstance()
                        .get(notifyRegistryServerConnId);
                    if (connection == null) {
                        connectToThisRegistryServerConnIds.remove(notifyRegistryServerConnId);
                    }
                    connection.writeAndFlush(
                        new Message(new Header(PacketType.REGISTRY), registryResponse));
                }
            }
        }
    }

    /**
     * 通知订阅者，服务发生注册
     * @author jiujie
     * 2016年5月16日 下午8:38:43
     * @param serviceInfo
     */
    private void notifyRpcConnectorRegister(ServiceInfo serviceInfo) {
        // notify register
        Set<Long> connectionIds = getConnIdSet(serviceInfo.getGroup(), serviceInfo.getName());
        if (connectionIds != null && connectionIds.size() > 0) {
            for (Long connectionId : connectionIds) {
                RegistryRequest request = new RegistryRequest(ApplicationType.REGISTRY_SERVER,
                    Commands.REGISTER, serviceInfo);
                Connection connection = ConnectionManager.getInstance().get(connectionId);
                if (connection == null) {
                    connectionIds.remove(connectionId);
                }
                connection.writeAndFlush(new Message(new Header(PacketType.REGISTRY), request));
            }
        }
    }

    public ServiceInfo[] getServices(String group, String serviceName) {
        if (StringUtils.isBlank(serviceName)) {
            return null;
        }
        ServiceInfoSet serviceInfoSet = serviceInfoContainer.get(group, serviceName);
        ServiceInfo[] serviceInfos = new ServiceInfo[serviceInfoSet.size()];
        serviceInfoSet.toArray(serviceInfos);
        return serviceInfos;
    }

    @Override
    public void unRegister(ApplicationType applicationType, ServiceInfo serviceInfo) {
        if (serviceInfo == null || StringUtils.isBlank(serviceInfo.getName())) {
            return;
        }
        serviceInfoContainer.remove(serviceInfo);

        // notify register
        Set<Long> connectionIds = getConnIdSet(serviceInfo.getGroup(), serviceInfo.getName());
        if (connectionIds != null && connectionIds.size() > 0) {
            for (Long connectionId : connectionIds) {
                RegistryRequest registryRequest = new RegistryRequest(
                    ApplicationType.REGISTRY_SERVER, Commands.UN_REGISTER, serviceInfo);
                Connection connection = ConnectionManager.getInstance().get(connectionId);
                if (connection == null) {
                    connectionIds.remove(connectionId);
                }
                connection
                    .writeAndFlush(new Message(new Header(PacketType.REGISTRY), registryRequest));
            }
        }

        if (applicationType.getValue() != ApplicationType.REGISTRY_SERVER.getValue()) {
            //1. notify RegistryConnectors
            if (registryConnections != null) {
                for (Connection connection : registryConnections) {
                    RegistryRequest registryRequest = new RegistryRequest(
                        ApplicationType.REGISTRY_SERVER, Commands.UN_REGISTER, serviceInfo);
                    connection.writeAndFlush(
                        new Message(new Header(PacketType.REGISTRY), registryRequest));
                }
            }
            //2. notify connected RegistryServers
            if (connectToThisRegistryServerConnIds != null) {
                for (long notifyRegistryServerConnId : connectToThisRegistryServerConnIds) {
                    RegistryResponse registryResponse = new RegistryResponse(Commands.UN_REGISTER,
                        serviceInfo);
                    Connection connection = ConnectionManager.getInstance()
                        .get(notifyRegistryServerConnId);
                    if (connection == null) {
                        connectToThisRegistryServerConnIds.remove(notifyRegistryServerConnId);
                    }
                    connection.writeAndFlush(
                        new Message(new Header(PacketType.REGISTRY), registryResponse));
                }
            }
        }
    }

    public List<ServiceInfo> getServiceInfos() {
        return serviceInfoContainer.getAll();
    }

    public void sycnServiceInfoContainer() {
        if (registryConnections == null || registryConnections.isEmpty()) {
            return;
        }
        Response response = null;
        try {
            response = RequestManager.getInstance().sysnRequest(registryConnections.get(0),
                new Header(PacketType.REGISTRY), new RegistryRequest(
                    ApplicationType.REGISTRY_SERVER, Commands.SYCN_SERVICE_INFO, null));
        } catch (RPCException e) {
            logger.error("", e);
            return;
        }
        List<ServiceInfo> result = (List<ServiceInfo>) response.getResult();
        serviceInfoContainer.addAll(result);
    }

}

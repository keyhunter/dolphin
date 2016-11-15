package com.dolphin.proxy;

import com.dolphin.core.config.RegistryConfig;
import com.dolphin.core.exception.RPCRunTimeException;
import com.dolphin.core.exception.ServiceInfoFormatException;
import com.dolphin.core.protocle.Connection;
import com.dolphin.core.protocle.ConnectionCloseListenser;
import com.dolphin.core.protocle.ConnectionManager;
import com.dolphin.core.protocle.transport.ServiceInfo;
import com.dolphin.registry.ServiceChangeListener;
import com.dolphin.registry.ServiceInfoContainer.ServiceInfoSet;
import com.dolphin.registry.consumer.AbstractServiceCustomer;
import com.dolphin.registry.consumer.ServiceCustomer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Service的Client的选择器
 * @author jiujie
 * @version $Id: ServiceClientSelector.java, v 0.1 2016年5月25日 下午10:18:49 jiujie Exp $
 */
public class ServiceConnectionSelector implements ConnectionSelector, ConnectionCloseListenser,
        ServiceChangeListener {

    private static Logger                    logger                        = Logger
        .getLogger(ServiceConnectionSelector.class);

    private Map<String, List<Long>>          serviceConnections;

    /** 重连列表 @author jiujie 2016年7月6日 上午10:18:36 */
    private ServiceInfoSet disConnectServiceInfos        = new ServiceInfoSet();

    private ServiceCustomer serviceCustomer;

    private static ServiceConnectionSelector selector                      = new ServiceConnectionSelector();

    private ScheduledExecutorService         executorService               = Executors
        .newSingleThreadScheduledExecutor();

    private static ConnectionManager         connectionManager             = ConnectionManager
        .getInstance();

    private static RPCConnector              rpcConnector;

    private Lock                             connectionLock                = new ReentrantLock();

    private static final String              SERVICE_KEY                   = "serviceKey";
    private static final String              SERVICE_INFO                  = "serviceInfo";

    private static final int                 EACH_SERVICE_CONNECTION_LIMIT = 2;

    private static final int                 RECONNECT_INTERVAL            = 3;

    private ServiceConnectionSelector() {
        rpcConnector = new RPCConnector();
        rpcConnector.startup();
        initCustomer();
        executorService.scheduleAtFixedRate(new ReconnectTask(), RECONNECT_INTERVAL,
            RECONNECT_INTERVAL, TimeUnit.SECONDS);
    }

    private void initCustomer() {
        AbstractServiceCustomer abstractServiceCustomer = null;
        try {
            abstractServiceCustomer = (AbstractServiceCustomer) Class
                .forName(RegistryConfig.getInstance().getCustomer()).newInstance();
        } catch (Exception e) {
            logger.error("", e);
            throw new RPCRunTimeException("Init customer failed");
        }
        abstractServiceCustomer.addServiceListener(this);
        serviceCustomer = abstractServiceCustomer;
        serviceConnections = new ConcurrentHashMap<>();
    }

    public static ConnectionSelector getInstance() {
        return selector;
    }

    @Override
    public Connection select(String group, String serviceName) {
        String serviceKey = getServiceKey(group, serviceName);
        List<Connection> connections = getConnections(serviceKey);
        if (connections == null || connections.isEmpty()) {
            resetServiceConnection(group, serviceName);
            connections = getConnections(serviceKey);
            if (connections == null || connections.isEmpty()) {
                return null;
            }
        }
        return connections.get(new Random().nextInt(connections.size()));
    }

    private List<Connection> getConnections(String serviceKey) {
        List<Long> connIds = serviceConnections.get(serviceKey);
        if (connIds == null || connIds.isEmpty()) {
            return null;
        }
        List<Connection> connections = new ArrayList<>();
        for (Long connId : connIds) {
            Connection connection = connectionManager.get(connId.longValue());
            if (connection != null) {
                connections.add(connection);
            }
        }
        return connections;
    }

    private String getServiceKey(String group, String serviceName) {
        String serviceKey = group + serviceName;
        return serviceKey;
    }

    private String getServiceKey(ServiceInfo serviceInfo) {
        String serviceKey = serviceInfo.getGroup() + serviceInfo.getName();
        return serviceKey;
    }

    /**
     * 关闭连接则从连接中去除这个客户端的连接
     * @author jiujie
     * 2016年6月6日 下午3:01:24
     * @see com.dolphin.core.protocle.ConnectionCloseListenser#close(com.dolphin.core.protocle.Connection)
     * @return
     */
    @Override
    public void close(Connection connection) {
        if (connection != null && connection.getAttribute(SERVICE_KEY) != null) {
            connectionLock.lock();
            try {
                String serviceKey;
                if (connection != null
                    && (serviceKey = (String) connection.getAttribute(SERVICE_KEY)) != null) {
                    if (StringUtils.isNotBlank(serviceKey)) {
                        List<Long> connIds = serviceConnections.get(serviceKey);
                        for (Long connId : connIds) {
                            if (connId == connection.getId()) {
                                connIds.remove(connId);
                                break;
                            }
                        }
                    }
                    ServiceInfo serviceInfo = (ServiceInfo) connection.getAttribute(SERVICE_INFO);
                    //将断开的连接加入重连列表
                    if (serviceInfo != null) {
                        disConnectServiceInfos.add(serviceInfo);
                    }
                }
            } finally {
                connectionLock.unlock();
            }
        }
    }

    @Override
    public void change(String group, String serviceName) {
        resetServiceConnection(group, serviceName);
    }

    /**
     * 重置Service服务的链接
     * @author jiujie
     * 2016年6月6日 下午3:24:54
     * @param group
     * @param serviceName
     */
    private void resetServiceConnection(String group, String serviceName) {
        connectionLock.lock();
        try {
            if (StringUtils.isBlank(group) || StringUtils.isBlank(serviceName)) {
                throw new ServiceInfoFormatException();
            }
            logger.info("ServiceInfo Changed [group:" + group + ",name:" + serviceName + "]");
            String serviceKey = getServiceKey(group, serviceName);
            List<Connection> connections = getConnections(serviceKey);
            ServiceInfo[] serviceInfos = serviceCustomer.getServices(group, serviceName);
            if (serviceInfos == null || serviceInfos.length == 0) {
                return;
            }
            ServiceInfo[] targets = random(serviceInfos, EACH_SERVICE_CONNECTION_LIMIT);
            if (targets == null || targets.length == 0) {
                return;
            }
            //如果没有连接
            if (connections == null || connections.isEmpty()) {
                for (ServiceInfo newServiceInfo : targets) {
                    if (disConnectServiceInfos.contains(newServiceInfo)) {
                        continue;
                    }
                    connect(newServiceInfo);
                }
            } else {
                for (Connection connection : connections) {
                    boolean exist = false;
                    for (ServiceInfo newServiceInfo : targets) {
                        ServiceInfo oldServiceInfo = (ServiceInfo) connection
                            .getAttribute(SERVICE_INFO);
                        if (oldServiceInfo != null && oldServiceInfo.equals(newServiceInfo)) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        connectionManager.remove(connection.getId());
                        connection.close();
                    }
                }
                for (ServiceInfo newServiceInfo : targets) {
                    if (disConnectServiceInfos.contains(newServiceInfo)) {
                        continue;
                    }
                    boolean exist = false;
                    for (Connection connection : connections) {
                        ServiceInfo oldServiceInfo = (ServiceInfo) connection
                            .getAttribute(SERVICE_INFO);
                        if (oldServiceInfo != null && oldServiceInfo.equals(newServiceInfo)) {
                            exist = true;
                            break;
                        }
                    }
                    if (exist) {
                        continue;
                    }
                    connect(newServiceInfo);
                }
            }
        } finally {
            connectionLock.unlock();
        }
    }

    private void connect(ServiceInfo serviceInfo) {
        Connection connection = rpcConnector.connect(serviceInfo.getHostAddress());
        connection.addCloseListener(this);
        String serviceKey = getServiceKey(serviceInfo);
        //连接内放放接口名等信息
        connection.setAttribute(SERVICE_KEY, serviceKey);
        connection.setAttribute(SERVICE_INFO, serviceInfo);
        List<Long> connIds = serviceConnections.get(serviceKey);
        if (connIds == null) {
            connIds = new ArrayList<>();
            serviceConnections.put(serviceKey, connIds);
        }
        connIds.add(connection.getId());
    }

    /**
     * 从一个ServiceInfo数组serviceInfos中随机出targetSize个ServiceInfo
     * @author jiujie
     * 2016年7月6日 上午10:57:16
     * @param serviceInfos
     * @param targetSize
     * @return
     */
    private ServiceInfo[] random(ServiceInfo[] serviceInfos, int targetSize) {
        int length = 0;
        if (serviceInfos == null || (length = serviceInfos.length) < targetSize) {
            return serviceInfos;
        }
        ServiceInfo[] targetServiceInfos = new ServiceInfo[targetSize];
        Set<Integer> targetIndexs = new HashSet<>();
        Random random = new Random();
        while (targetIndexs.size() < targetSize) {
            targetIndexs.add(random.nextInt(length));
        }
        Iterator<Integer> iterator = targetIndexs.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            targetServiceInfos[i] = serviceInfos[next];
            i++;
        }
        return targetServiceInfos;
    }

    /**
     * 重连任务
     * @author jiujie
     * @version $Id: ServiceConnectionSelector.java, v 0.1 2016年7月6日 上午11:56:32 jiujie Exp $
     */
    public class ReconnectTask implements Runnable {

        @Override
        public void run() {
            Thread.currentThread().setName("reconnectTask");
            connectionLock.lock();
            try {
                Iterator<ServiceInfo> iterator = disConnectServiceInfos.iterator();
                while (iterator.hasNext()) {
                    ServiceInfo serviceInfo = iterator.next();
                    if (logger.isInfoEnabled()) {
                        logger.info("Start reconnect to service [" + serviceInfo + "]");
                    }
                    String serviceKey = getServiceKey(serviceInfo);
                    List<Connection> connections = getConnections(serviceKey);
                    if (connections == null || connections.isEmpty()) {
                        connect(serviceInfo);
                        disConnectServiceInfos.remove(serviceInfo);
                    } else {
                        boolean exist = false;
                        for (Connection connection : connections) {
                            if (serviceInfo != null
                                && serviceInfo.equals(connection.getAttribute(SERVICE_INFO))) {
                                exist = true;
                                break;
                            }
                        }
                        if (exist) {
                            continue;
                        }
                        connect(serviceInfo);
                        disConnectServiceInfos.remove(serviceInfo);
                    }
                }
            } finally {
                connectionLock.unlock();
            }
        }

    }

}

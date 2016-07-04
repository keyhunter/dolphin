package com.dolphin.rpc.proxy;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.dolphin.rpc.core.config.RegistryConfig;
import com.dolphin.rpc.core.exception.RPCRunTimeException;
import com.dolphin.rpc.core.exception.ServiceInfoFormatException;
import com.dolphin.rpc.core.io.Connection;
import com.dolphin.rpc.core.io.ConnectionCloseListenser;
import com.dolphin.rpc.registry.AbstractServiceCustomer;
import com.dolphin.rpc.registry.ServiceChangeListener;
import com.dolphin.rpc.registry.ServiceInfo;
import com.dolphin.rpc.registry.consumer.ServiceCustomer;

/**
 * Service的Client的选择器
 * @author jiujie
 * @version $Id: ServiceClientSelector.java, v 0.1 2016年5月25日 下午10:18:49 jiujie Exp $
 */
public class ServiceConnectionSelector implements ConnectionSelector, ConnectionCloseListenser,
                                       ServiceChangeListener {

    private static Logger                    logger       = Logger
        .getLogger(ServiceConnectionSelector.class);

    private Map<String, Connection>          serviceConnections;

    private ServiceCustomer                  serviceCustomer;

    private static ServiceConnectionSelector selector     = new ServiceConnectionSelector();

    private static RPCConnector              rpcConnector;

    private static final String              SERVICE_KEY  = "serviceKey";
    private static final String              SERVICE_INFO = "serviceInfo";

    private ServiceConnectionSelector() {
        rpcConnector = new RPCConnector();
        rpcConnector.startup();
        initCustomer();
    }

    private void initCustomer() {
        AbstractServiceCustomer abstractServiceCustomer = null;
        try {
            abstractServiceCustomer = (AbstractServiceCustomer) Class
                .forName(new RegistryConfig().getCustomer()).newInstance();
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
        Connection connection = serviceConnections.get(serviceKey);
        if (connection == null) {
            synchronized (this) {
                if (connection == null) {
                    ServiceInfo[] serviceInfos = serviceCustomer.getServices(group, serviceName);
                    if (serviceInfos == null || serviceInfos.length == 0) {
                        return null;
                    }

                    //负载均衡，这里随机一个 TODO 之后可以 写的更复杂一下
                    ServiceInfo serviceInfo = serviceInfos[new Random()
                        .nextInt(serviceInfos.length)];
                    //订阅服务
                    serviceCustomer.subcride(group, serviceName);
                    connection = rpcConnector.connect(serviceInfo.getHostAddress());
                    //连接内放放接口名等信息
                    connection.setAttribute(SERVICE_KEY, serviceKey);
                    connection.setAttribute(SERVICE_INFO, serviceInfo);
                    serviceConnections.put(serviceKey, connection);
                    return connection;
                }
            }
        }
        return connection;
    }

    private String getServiceKey(String group, String serviceName) {
        String serviceKey = group + serviceName;
        return serviceKey;
    }

    /**
     * 关闭连接则从连接中去除这个客户端的连接
     * @author jiujie
     * 2016年6月6日 下午3:01:24
     * @see com.dolphin.rpc.core.io.ConnectionCloseListenser#close(com.dolphin.rpc.core.io.Connection)
     * @return 
     */
    @Override
    public void close(Connection connection) {
        if (connection != null && connection.getAttribute(SERVICE_KEY) != null) {
            connection.getAttribute(SERVICE_KEY);
            String serviceKey = (String) connection.getAttribute(SERVICE_KEY);
            serviceConnections.remove(serviceKey);
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
     * @param serviceInfo
     */
    private void resetServiceConnection(String group, String serviceName) {
        if (StringUtils.isBlank(group) || StringUtils.isBlank(serviceName)) {
            throw new ServiceInfoFormatException();
        }
        logger.info("ServiceInfo Changed [group:" + group + ",name:" + serviceName + "]");
        Connection connection = serviceConnections.get(getServiceKey(group, serviceName));
        if (connection != null) {
            ServiceInfo oldServiceInfo = (ServiceInfo) connection.getAttribute(SERVICE_INFO);
            ServiceInfo[] serviceInfos = serviceCustomer.getServices(group, serviceName);
            if (serviceInfos == null) {
                return;
            }
            //负载均衡，这里随机一个 TODO 之后可以 写的更复杂一下
            ServiceInfo newServiceInfo = serviceInfos[new Random().nextInt(serviceInfos.length)];
            if (newServiceInfo != null
                && !newServiceInfo.getHostAddress().equals(oldServiceInfo.getHostAddress()))
                connection = rpcConnector.connect(newServiceInfo.getHostAddress());
        }
    }

}

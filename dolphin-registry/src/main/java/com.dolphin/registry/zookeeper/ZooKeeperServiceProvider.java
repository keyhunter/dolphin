package com.dolphin.registry.zookeeper;

import com.dolphin.core.protocle.transport.ServiceInfo;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.dolphin.core.exception.RPCRunTimeException;
import com.dolphin.core.protocle.HostAddress;
import com.dolphin.registry.MySQLRegistryAddressContainer;
import com.dolphin.registry.provider.AbstractServiceProvider;
import com.dolphin.registry.provider.ServiceProvider;
import com.dolphin.registry.zookeeper.connector.ZookeeperConnector;
import com.dolphin.registry.zookeeper.listener.AbstractWatcherListener;

/**
 * zookeeper 服务提供service
 * @version $Id: ZooKeeperServiceProvider.java, v 0.1 2016年7月19日 上午10:45:47 tianxiao Exp $
 */
public class ZooKeeperServiceProvider extends AbstractServiceProvider implements ServiceProvider {

    private static Logger logger      = Logger.getLogger(ZooKeeperServiceProvider.class);

    private ZooKeeper     zooKeeper;

    /**zookeeper操作失败 重新执行次数  */
    private final int     RETRY_TIMES = 3;

    public ZooKeeperServiceProvider(ServiceInfo serviceInfo) {
        super(serviceInfo);
        try {
            zooKeeper = new ZookeeperConnector(new ProviderWatcherListener(), true,
                MySQLRegistryAddressContainer.getInstance()).connect();
            registerSelf();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * zookeeper watcher 监听器实现
     * @author tianxiao
     * @version $Id: ZooKeeperServiceProvider.java, v 0.1 2016年7月19日 上午10:23:02 tianxiao Exp $
     */
    class ProviderWatcherListener extends AbstractWatcherListener {
        /**
         * 实现重连之后，将重连后的zookeeper对象重新赋值并重新注册自己
         * @author tianxiao
         * @see com.dolphin.rpc.registry.zookeeper.listener.AbstractWatcherListener#afterReconnect(org.apache.zookeeper.WatchedEvent, org.apache.zookeeper.ZooKeeper)
         */
        @Override
        public void timeout(WatchedEvent watchedEvent, ZooKeeper args) {
            zooKeeper = args;
            registerSelf();
        }
    }

    /**
     * 注册服务
     * @see com.dolphin.rpc.registry.provider.ServiceProvider#register(com.dolphin.rpc.registry.ServiceInfo)
     */
    @Override
    public void register(ServiceInfo serviceInfo) {
        if (serviceInfo == null) {
            return;
        }
        createPath("/service", CreateMode.PERSISTENT);
        createPath("/service/" + serviceInfo.getGroup(), CreateMode.PERSISTENT);
        createPath("/service/" + serviceInfo.getGroup() + "/" + serviceInfo.getName(),
            CreateMode.PERSISTENT);
        createPath("/service/" + serviceInfo.getGroup() + "/" + serviceInfo.getName() + "/"
                   + serviceInfo.getHostAddress().getHost() + ":"
                   + serviceInfo.getHostAddress().getPort(), CreateMode.EPHEMERAL);
    }

    /**
     * 用所给的路径创建zookeeper节点
     * @author tianxiao
     * @param path
     * @param createMode
     * @version 2016年7月19日 上午10:47:42 tianxiao
     */
    private void createPath(String path, CreateMode createMode) {
        for (int i = 0; i < RETRY_TIMES; i++) {
            try {
                zooKeeper.create(path, null, Ids.OPEN_ACL_UNSAFE, createMode);
            } catch (KeeperException e) {
                if (e instanceof NodeExistsException) {
                    return;
                }
                logger.error("", e);
            } catch (InterruptedException e) {
                logger.error("", e);
                continue;
            }
        }
        throw new RPCRunTimeException("Create path [" + path + "] faild.");
    }

    /**
     * 通过服务信息获取该服务在zookeeper中存储路径
     * @author tianxiao
     * @param serviceInfo
     * @return
     * @version 2016年7月19日 上午10:48:47 tianxiao
     */
    private String getPath(ServiceInfo serviceInfo) {
        if (serviceInfo == null) {
            return null;
        }
        return "/service/" + serviceInfo.getGroup() + "/" + serviceInfo.getName() + "/"
               + serviceInfo.getHostAddress().getHost() + ":"
               + serviceInfo.getHostAddress().getPort();
    }

    /**
     * 注销服务
     * @see com.dolphin.rpc.registry.provider.ServiceProvider#unRegister(com.dolphin.rpc.registry.ServiceInfo)
     */
    @Override
    public void unRegister(ServiceInfo serviceInfo) {
        if (serviceInfo == null) {
            return;
        }
        for (int i = 0; i < RETRY_TIMES; i++) {
            try {
                zooKeeper.delete(getPath(serviceInfo), 0);
                return;
            } catch (KeeperException | InterruptedException e) {
                logger.error("", e);
            }
        }
        throw new RPCRunTimeException("unRegister Service [" + serviceInfo.toString() + "] faild.");
    }

    public static void main(String[] args) {
        ZooKeeperServiceProvider serviceProvider = new ZooKeeperServiceProvider(new ServiceInfo(
            "test", "xxx", new HostAddress("192.168.2.2", 2342)));
        while (true) {
        }
    }

}

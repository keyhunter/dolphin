package com.dolphin.rpc.registry.zookeeper;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.dolphin.rpc.core.exception.RPCRunTimeException;
import com.dolphin.rpc.core.io.HostAddress;
import com.dolphin.rpc.registry.ServiceInfo;
import com.dolphin.rpc.registry.provider.ServiceProvider;

public class ZooKeeperServiceProvider implements ServiceProvider {

    private static Logger  logger         = Logger.getLogger(ZooKeeperServiceProvider.class);

    private ZooKeeper      zooKeeper;

    private final int      RETRY_TIMES    = 3;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public ZooKeeperServiceProvider() {
        try {
            zooKeeper = new ZooKeeper("10.1.2.85", 5000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == KeeperState.SyncConnected) {
                        countDownLatch.countDown();
                    }

                    System.out.println("已经触发了" + event.getType() + "事件！");
                }
            });
            countDownLatch.await();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

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
                   + serviceInfo.getHostAddress().getPort(),
            CreateMode.EPHEMERAL);
    }

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

    private String getPath(ServiceInfo serviceInfo) {
        if (serviceInfo == null) {
            return null;
        }
        return "/service/" + serviceInfo.getGroup() + "/" + serviceInfo.getName() + "/"
               + serviceInfo.getHostAddress().getHost() + ":"
               + serviceInfo.getHostAddress().getPort();
    }

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
        ZooKeeperServiceProvider serviceProvider = new ZooKeeperServiceProvider();
        serviceProvider
            .register(new ServiceInfo("test", "xxx", new HostAddress("192.168.2.2", 2342)));
    }

}

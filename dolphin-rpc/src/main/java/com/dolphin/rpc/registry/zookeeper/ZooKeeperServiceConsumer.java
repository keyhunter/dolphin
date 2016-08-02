package com.dolphin.rpc.registry.zookeeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

import com.dolphin.rpc.core.io.HostAddress;
import com.dolphin.rpc.registry.MySQLRegistryAddressContainer;
import com.dolphin.rpc.registry.ServiceInfo;
import com.dolphin.rpc.registry.consumer.AbstractServiceCustomer;
import com.dolphin.rpc.registry.zookeeper.connector.ZookeeperConnector;
import com.dolphin.rpc.registry.zookeeper.listener.AbstractWatcherListener;

/**
 * ZooKeeper实现的Service的消费者
 * @author jiujie
 * @version $Id: ZooKeeperServiceConsumer.java, v 0.1 2016年6月30日 下午4:01:21 jiujie Exp $
 */
public class ZooKeeperServiceConsumer extends AbstractServiceCustomer {

    private static Logger        logger              = Logger
                                                         .getLogger(ZooKeeperServiceConsumer.class);

    private ZooKeeper            zooKeeper;

    /**zookeeper操作失败 重新执行次数  */
    private final int            RETRY_TIMES         = 3;

    private final static String  PARENT_REGEX        = "^/service/([a-zA-Z]+)/([a-zA-Z]+)";

    private final static Pattern PARENT_PATH_PATTERN = Pattern.compile(PARENT_REGEX);

    private final static String  PATH_REGEX          = "^/service/([a-zA-Z]+)/([a-zA-Z]+)/(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)";

    private final static Pattern PATH_PATTERN        = Pattern.compile(PATH_REGEX);

    public ZooKeeperServiceConsumer() {
        try {
            zooKeeper = new ZookeeperConnector(new ConsumerWatcherListener(), true,
                MySQLRegistryAddressContainer.getInstance()).connect();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * zookeeper watcher 监听器实现
     * @author tianxiao
     * @version $Id: ZooKeeperServiceProvider.java, v 0.1 2016年7月19日 上午10:23:02 tianxiao Exp $
     */
    class ConsumerWatcherListener extends AbstractWatcherListener {
        @Override
        public void timeout(WatchedEvent watchedEvent, ZooKeeper args) {
            zooKeeper = args;
        }

        /**
         * @author tianxiao
         * @see com.dolphin.rpc.registry.zookeeper.listener.AbstractWatcherListener#after(org.apache.zookeeper.WatchedEvent)
         */
        @Override
        public void after(WatchedEvent watchedEvent) {
            String path = watchedEvent.getPath();
            if (path == null) {
                return;
            }
            if (path.matches(PARENT_REGEX)) {
                EventType type = watchedEvent.getType();
                if (type == null) {
                    return;
                }
                if (type.getIntValue() == EventType.NodeChildrenChanged.getIntValue()) {
                    change(getGroup(path), getServiceName(path));
                }
            }
        }
    }

    /**
     * 通过zookeeper存储路径获取服务名称
     * @author tianxiao
     * @param path
     * @return
     * @version 2016年7月19日 上午11:03:11 tianxiao
     */
    private String getServiceName(String path) {
        if (path == null || StringUtils.isBlank(path)) {
            return null;
        }
        if (!path.matches(PARENT_REGEX)) {
            return null;
        }
        Matcher matcher = PARENT_PATH_PATTERN.matcher(path);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }

    /**
     * 通过zookeeper存储路径获取服务组名称
     * @author tianxiao
     * @param path
     * @return
     * @version 2016年7月19日 上午11:04:03 tianxiao
     */
    private String getGroup(String path) {
        if (path == null || StringUtils.isBlank(path)) {
            return null;
        }
        if (!path.matches(PARENT_REGEX)) {
            return null;
        }
        Matcher matcher = PARENT_PATH_PATTERN.matcher(path);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 通过zookeeper存储路径获取服务信息
     * @author tianxiao
     * @param path
     * @return
     * @version 2016年7月19日 上午11:04:26 tianxiao
     */
    private ServiceInfo getServiceInfo(String path) {
        if (path == null || StringUtils.isBlank(path)) {
            return null;
        }
        if (!path.matches(PATH_REGEX)) {
            return null;
        }
        Matcher matcher = PATH_PATTERN.matcher(path);
        if (matcher.find()) {
            ServiceInfo serviceInfo = new ServiceInfo(matcher.group(1), matcher.group(2),
                new HostAddress(matcher.group(3), Integer.valueOf(matcher.group(4))));
            return serviceInfo;
        }
        return null;
    }

    public static void main(String[] args) {
        ZooKeeperServiceConsumer zs = new ZooKeeperServiceConsumer();
        ServiceInfo[] services = zs.getServices("test", "cmsService");
        System.out.println(Arrays.deepToString(services));
        while (true) {
        }
    }

    @Override
    public void subcride(String group, String serviceName) {
        //zookeeper在查询的时候可以设置watch
    }

    @Override
    public void unSubcride(String group, String serviceName) {
        //zookeeper在查询的时候可以设置watch
    }

    /**
     * @see com.dolphin.rpc.registry.consumer.AbstractServiceCustomer#getRemoteServiceInfos(java.lang.String, java.lang.String)
     */
    @Override
    protected ServiceInfo[] getRemoteServiceInfos(String group, String serviceName) {
        String path = "/service/" + group + "/" + serviceName;
        for (int i = 0; i < RETRY_TIMES; i++) {
            try {
                return getChiledren(path);
            } catch (KeeperException | InterruptedException e) {
                logger.error("", e);
                continue;
            }
        }

        return null;
    }

    /**
     * 通过存储路径获取该路径所有子路径下的服务
     * @author tianxiao
     * @param path
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     * @version 2016年7月19日 上午11:08:32 tianxiao
     */
    private ServiceInfo[] getChiledren(String path) throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren(path, true);
        if (children == null || children.isEmpty()) {
            return null;
        }
        List<ServiceInfo> serviceInfos = new ArrayList<>();
        for (String child : children) {
            ServiceInfo serviceInfo = getServiceInfo(path + "/" + child);
            if (serviceInfo != null) {
                serviceInfos.add(serviceInfo);
            }
        }
        ServiceInfo[] serviceInfoArr = new ServiceInfo[serviceInfos.size()];
        return serviceInfos.toArray(serviceInfoArr);
    }

}

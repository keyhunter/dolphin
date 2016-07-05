package com.dolphin.rpc.registry.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import com.dolphin.rpc.core.io.HostAddress;
import com.dolphin.rpc.registry.ServiceInfo;
import com.dolphin.rpc.registry.consumer.AbstractServiceCustomer;

/**
 * ZooKeeper实现的Service的消费者
 * @author jiujie
 * @version $Id: ZooKeeperServiceConsumer.java, v 0.1 2016年6月30日 下午4:01:21 jiujie Exp $
 */
public class ZooKeeperServiceConsumer extends AbstractServiceCustomer {

    private static Logger        logger              = Logger
        .getLogger(ZooKeeperServiceConsumer.class);

    private ZooKeeper            zooKeeper;

    private final int            RETRY_TIMES         = 3;

    private final int            SESSION_TIME_OUT    = 300;

    private final static String  PARENT_REGEX        = "^/service/([a-zA-Z]+)/([a-zA-Z]+)";

    private final static Pattern PARENT_PATH_PATTERN = Pattern.compile(PARENT_REGEX);

    private final static String  PATH_REGEX          = "^/service/([a-zA-Z]+)/([a-zA-Z]+)/(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)";

    private final static Pattern PATH_PATTERN        = Pattern.compile(PATH_REGEX);

    private CountDownLatch       countDownLatch;

    //注册中心的监听器
    private Watcher              nodeWatcher         = new RegistryWatcher();

    public ZooKeeperServiceConsumer() {
        try {
            countDownLatch = new CountDownLatch(1);
            zooKeeper = new ZooKeeper("10.1.2.85", SESSION_TIME_OUT, nodeWatcher, true);
            countDownLatch.await();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 注册中心监视器
     * @author jiujie
     * @version $Id: ZooKeeperServiceConsumer.java, v 0.1 2016年7月1日 下午5:25:28 jiujie Exp $
     */
    public class RegistryWatcher implements Watcher {

        @Override
        public void process(WatchedEvent watchedEvent) {
            if (watchedEvent.getState() == KeeperState.SyncConnected) {
                countDownLatch.countDown();
            }
            if (watchedEvent.getState() == KeeperState.Expired) {
                try {
                    zooKeeper.close();
                } catch (Exception e) {
                    logger.error("", e);
                }
                try {
                    logger.info("Reconnect to zookeeper server.");
                    countDownLatch = new CountDownLatch(1);
                    zooKeeper = new ZooKeeper("10.1.2.85", SESSION_TIME_OUT, nodeWatcher, true);
                    countDownLatch.await();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
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

    private ServiceInfo getServiceInfo(String path) {
        if (path == null || StringUtils.isBlank(path)) {
            return null;
        }
        if (!path.matches(PATH_REGEX)) {
            return null;
        }
        Matcher matcher = PATH_PATTERN.matcher(path);
        if (matcher.find()) {
            ServiceInfo serviceInfo = new ServiceInfo(matcher.group(2), matcher.group(1),
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

package com.dolphin.rpc.registry.zookeeper.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

import com.dolphin.rpc.registry.zookeeper.listener.WatcherListener;

/**
 * zookeeper连接器
 * @author tianxiao
 * @version $Id: ZookeeperConnector.java, v 0.1 2016年7月19日 上午10:43:12 tianxiao Exp $
 */
public class ZookeeperConnector {
    private static Logger         logger               = Logger.getLogger(ZookeeperConnector.class);
    private ZooKeeper             zooKeeper;
    private CountDownLatch        countDownLatch       = null;
    /**zookeeper session timeout 时间  */
    private final int             SESSION_TIME_OUT     = 300;
    private String                connectString;
    /**是否需要重连，默认为true  */
    private boolean               needReconnect        = true;
    /**zookeeper 重连次数默认值  */
    private static final int      INIT_RECONNECT_TIMES = 20;
    /**zookeeper 重连次数  */
    private int                   reconnectTimes       = INIT_RECONNECT_TIMES;
    /**最大的线程睡眠时间  */
    private static final long     MAX_INTERVAL         = 1000 * 5;
    /**注册中心的监听器*/
    private Watcher               nodeWatcher          = new RegistryWatcher();
    /**watcher 监听器  */
    private List<WatcherListener> watcherListeners;

    /**
     * zookeeper连接器构造器
     * @param watcherListeners 监视器的监听器
     * @param connectString zookeeper连接地址  ip:port, 多个用逗号连接
     */
    public ZookeeperConnector(List<WatcherListener> watcherListeners, String connectString) {
        this.watcherListeners = watcherListeners;
        this.connectString = connectString;
    }

    /**
     * zookeeper连接器构造器
     * @param watcherListeners 监视器的监听器
     * @param needReconnect 是否需要重连
     * @param connectString zookeeper连接地址  ip:port, 多个用逗号连接
     */
    public ZookeeperConnector(List<WatcherListener> watcherListeners, boolean needReconnect,
                              String connectString) {
        this.needReconnect = needReconnect;
        this.watcherListeners = watcherListeners;
        this.connectString = connectString;
    }

    /**
     * zookeeper连接器构造器
     * @param watcherListener 监视器的监听器
     * @param needReconnect 是否需要重连
     * @param connectString zookeeper连接地址  ip:port, 多个用逗号连接
     */
    public ZookeeperConnector(WatcherListener watcherListener, boolean needReconnect,
                              String connectString) {
        this.needReconnect = needReconnect;
        this.watcherListeners = new ArrayList<WatcherListener>();
        watcherListeners.add(watcherListener);
        this.connectString = connectString;
    }

    /**zookeeper 连接
     * @author tianxiao
     * @return
     * @version 2016年7月18日 下午4:39:20 tianxiao
     */
    public ZooKeeper connect() {
        try {
            countDownLatch = new CountDownLatch(1);
            zooKeeper = new ZooKeeper(connectString, SESSION_TIME_OUT, nodeWatcher, true);
            countDownLatch.await();
        } catch (Exception e) {
            logger.error("", e);
        }
        return zooKeeper;
    }

    /**
     * 注册中心监视器
     * @author tianxiao
     * @version $Id: ZookeeperConnector.java, v 0.1 2016年7月18日 下午4:39:49 tianxiao Exp $
     */
    public class RegistryWatcher implements Watcher {

        @Override
        public void process(WatchedEvent watchedEvent) {
            if (watchedEvent.getState() == KeeperState.SyncConnected) {
                countDownLatch.countDown();
                resetReconnectTimes();
            }
            if (watchedEvent.getState() == KeeperState.Expired) {
                if (needReconnect) {
                    try {
                        Thread.sleep(getReconnectInterval(reconnectTimes));
                        if (zooKeeper != null) {
                            zooKeeper.close();
                        }
                        logger.info("Reconnect to zookeeper server.");
                        countDownLatch = new CountDownLatch(1);
                        zooKeeper = new ZooKeeper(connectString, SESSION_TIME_OUT, nodeWatcher,
                            true);
                        countDownLatch.await();
                        if (zooKeeper.getState() == States.CONNECTED) {
                            logger.info("Reconnect to zookeeper server complite.");
                            excuteWatcherListenersDoAfterReconnect(watchedEvent, zooKeeper);
                        }
                        reconnectTimes--;
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
            }
            excuteWatcherListenersDoAfter(watchedEvent);
        }
    }

    /**
     * 获取重连时间间隔（重连时间间隔规则）
     * @author tianxiao
     * @param reconnectTimes
     * @return
     * @version 2016年7月6日 下午11:54:51 tianxiao
     */
    private long getReconnectInterval(int reconnectTimes) {
        if (reconnectTimes > 10 && reconnectTimes <= 20) {
            return 1L;
        }
        if (reconnectTimes > 5 && reconnectTimes <= 10) {
            return 100L * 3L;
        }
        if (reconnectTimes > 0 && reconnectTimes <= 5) {
            return 1000L * 1L;
        }
        return MAX_INTERVAL;
    }

    /**
     * 重置重连次数
     * @author tianxiao
     * @version 2016年7月19日 上午10:41:46 tianxiao
     */
    private void resetReconnectTimes() {
        reconnectTimes = INIT_RECONNECT_TIMES;
    }

    /**
     * 执行所有watcher监听器的timeout方法
     * @author tianxiao
     * @param watchedEvent
     * @param zooKeeper
     * @version 2016年7月19日 上午10:42:04 tianxiao
     */
    private void excuteWatcherListenersDoAfterReconnect(WatchedEvent watchedEvent,
                                                        ZooKeeper zooKeeper) {
        if (watcherListeners != null && watcherListeners.size() > 0) {
            for (WatcherListener watcherListener : watcherListeners) {
                watcherListener.timeout(watchedEvent, zooKeeper);
            }
        }
    }

    /**
     * 执行所有watcher监听器after方法
     * @author tianxiao
     * @param watchedEvent
     * @version 2016年7月19日 上午10:42:26 tianxiao
     */
    private void excuteWatcherListenersDoAfter(WatchedEvent watchedEvent) {
        if (watcherListeners != null && watcherListeners.size() > 0) {
            for (WatcherListener watcherListener : watcherListeners) {
                watcherListener.after(watchedEvent);
            }
        }
    }
}

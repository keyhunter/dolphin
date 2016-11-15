package com.dolphin.registry.zookeeper.listener;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

public interface WatcherListener {
    
    /**
     * timeout监听
     * @author tianxiao
     * @param watchedEvent
     * @param zooKeeper
     * @version 2016年7月18日 下午5:52:14 tianxiao
     */
    void timeout(WatchedEvent watchedEvent, ZooKeeper zooKeeper);
    
    /**在方法执行最后监听
     * @author tianxiao
     * @param watchedEvent
     * @version 2016年7月18日 下午5:52:43 tianxiao
     */
    void after(WatchedEvent watchedEvent);
}

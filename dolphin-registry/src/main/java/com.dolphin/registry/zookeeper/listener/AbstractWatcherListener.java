package com.dolphin.registry.zookeeper.listener;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

public class AbstractWatcherListener implements WatcherListener {
    
    @Override
    public void after(WatchedEvent watchedEvent) {
    }

    @Override
    public void timeout(WatchedEvent watchedEvent, ZooKeeper zooKeeper) {
    }
}

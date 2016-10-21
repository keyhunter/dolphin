package com.dolphin.rpc.core.io;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ConnectionManager {

    private static volatile ConnectionManager channelManager;

    private AtomicLong                        atomicLong  = new AtomicLong();

    private Map<Long, Connection>             connections = new ConcurrentHashMap<>();

    public static ConnectionManager getInstance() {
        if (channelManager == null) {
            synchronized (ConnectionManager.class) {
                if (channelManager == null) {
                    channelManager = new ConnectionManager();
                }
            }
        }
        return channelManager;
    }

    public Connection create(Connection connection) {
        long id = atomicLong.incrementAndGet();
        AbstractConnection abstractConnection = (AbstractConnection) connection;
        abstractConnection.setId(id);
        connections.put(id, abstractConnection);
        return abstractConnection;
    }

    public Connection get(long id) {
        return connections.get(id);
    }

    public Connection remove(long id) {
        return connections.remove(id);
    }

}

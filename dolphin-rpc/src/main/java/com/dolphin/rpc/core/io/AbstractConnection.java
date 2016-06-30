package com.dolphin.rpc.core.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractConnection implements Connection {

    private long                           id;

    /** 参数  @author jiujie 2016年6月1日 上午10:43:03 */
    private Map<String, Object>            attributes      = new HashMap<>();

    private List<ConnectionCloseListenser> closeListensers = new ArrayList<>();

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void addCloseListener(ConnectionCloseListenser closeListenser) {
        closeListensers.add(closeListenser);
    }

    @Override
    public void close() {
        for (ConnectionCloseListenser closeListenser : closeListensers) {
            closeListenser.close(this);
        }
    }

}

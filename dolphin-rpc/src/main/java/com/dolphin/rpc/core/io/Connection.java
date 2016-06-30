package com.dolphin.rpc.core.io;

/**
 * 客户端与服务器之间虚拟的连接，可以发送消息
 * @author jiujie
 * @version $Id: Connection.java, v 0.1 2016年5月12日 上午11:56:48 jiujie Exp $
 */
public interface Connection {

    void close();

    long getId();

    void setAttribute(String key, Object value);

    void removeAttribute(String key);

    Object getAttribute(String key);

    void write(Object object);

    void writeAndFlush(Object object);

    void addCloseListener(ConnectionCloseListenser closeListenser);

}

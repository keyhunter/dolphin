package com.dolphin.rpc.core.io;

/**
 * 客户端与服务器之间虚拟的连接，可以发送消息
 * @author jiujie
 * @version $Id: Connection.java, v 0.1 2016年5月12日 上午11:56:48 jiujie Exp $
 */
public interface Connection {

    /**
     * 关闭连接
     * @author jiujie
     * 2016年7月18日 上午11:15:21
     */
    void close();

    /**
     * 查询连接是否关闭
     * @author jiujie
     * 2016年7月18日 上午11:15:27
     * @return
     */
    boolean isClose();

    /**
     * 获取连接ID
     * @author jiujie
     * 2016年7月18日 上午11:15:37
     * @return
     */
    long getId();

    /**
     * 设置连接属性
     * @author jiujie
     * 2016年7月18日 上午11:15:43
     * @param key
     * @param value
     */
    void setAttribute(String key, Object value);

    /**
     * 移除连接属性
     * @author jiujie
     * 2016年7月18日 上午11:15:54
     * @param key
     */
    void removeAttribute(String key);

    /**
     * 获取连接属性
     * @author jiujie
     * 2016年7月18日 上午11:16:05
     * @param key
     * @return
     */
    Object getAttribute(String key);

    /**
     * 通过连接发送对象
     * @author jiujie
     * 2016年7月18日 上午11:16:13
     * @param object
     */
    void write(Object object);

    /**
     * 通过连接发送对象并进行flush
     * @author jiujie
     * 2016年7月18日 上午11:16:28
     * @param object
     */
    void writeAndFlush(Object object);

    /**
     * 加入连接关闭监听器
     * @author jiujie
     * 2016年7月18日 上午11:16:43
     * @param closeListenser
     */
    void addCloseListener(ConnectionCloseListenser closeListenser);

}

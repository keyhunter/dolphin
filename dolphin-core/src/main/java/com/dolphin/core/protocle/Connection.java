package com.dolphin.core.protocle;

/**
 * 客户端与服务器之间虚拟的连接，可以发送消息
 *
 * @author keyhunter
 * @version $Id: Connection.java, v 0.1 2016年5月12日 上午11:56:48 keyhunter Exp $
 */
public interface Connection {

    /**
     * 关闭连接
     *
     * @author keyhunter
     * 2016年7月18日 上午11:15:21
     */
    void close();

    /**
     * 查询连接是否关闭
     *
     * @return
     * @author keyhunter
     * 2016年7月18日 上午11:15:27
     */
    boolean isClose();

    /**
     * 获取连接ID
     *
     * @return
     * @author keyhunter
     * 2016年7月18日 上午11:15:37
     */
    long getId();

    /**
     * 设置连接属性
     *
     * @param key
     * @param value
     * @author keyhunter
     * 2016年7月18日 上午11:15:43
     */
    void setAttribute(String key, Object value);

    /**
     * 移除连接属性
     *
     * @param key
     * @author keyhunter
     * 2016年7月18日 上午11:15:54
     */
    void removeAttribute(String key);

    /**
     * 获取连接属性
     *
     * @param key
     * @return
     * @author keyhunter
     * 2016年7月18日 上午11:16:05
     */
    Object getAttribute(String key);

    /**
     * 通过连接发送对象
     *
     * @param object
     * @author keyhunter
     * 2016年7月18日 上午11:16:13
     */
    void write(Object object);

    /**
     * 通过连接发送对象并进行flush
     *
     * @param object
     * @author keyhunter
     * 2016年7月18日 上午11:16:28
     */
    void writeAndFlush(Object object);

    /**
     * 加入连接关闭监听器
     *
     * @param closeListenser
     * @author keyhunter
     * 2016年7月18日 上午11:16:43
     */
    void addCloseListener(ConnectionCloseListenser closeListenser);

}

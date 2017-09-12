package com.dolphin.core.protocle;

/**
 * 连接关闭监听器
 *
 * @author keyhunter
 * @version $Id: ConnectionCloseListenser.java, v 0.1 2016年6月6日 下午2:16:01 keyhunter Exp $
 */
public interface ConnectionCloseListenser {

    /**
     * 连接关闭
     *
     * @param connection
     * @author keyhunter
     * 2016年6月6日 下午2:15:58
     */
    void close(Connection connection);

}

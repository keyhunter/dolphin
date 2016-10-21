package com.dolphin.rpc.core.io;

/**
 * 连接关闭监听器
 * @author jiujie
 * @version $Id: ConnectionCloseListenser.java, v 0.1 2016年6月6日 下午2:16:01 jiujie Exp $
 */
public interface ConnectionCloseListenser {

    /**
     * 连接关闭
     * @author jiujie
     * 2016年6月6日 下午2:15:58
     * @param connection
     */
    void close(Connection connection);

}

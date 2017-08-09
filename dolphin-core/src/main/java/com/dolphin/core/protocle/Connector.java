package com.dolphin.core.protocle;

import com.dolphin.core.Application;

/**
 * 连接客户端
 * @author keyhunter
 * @version $Id: Connector.java, v 0.1 2016年5月13日 上午11:02:14 keyhunter Exp $
 */
public interface Connector extends Application {

    /**
     * 连接一个地址
     * @author keyhunter
     * 2016年6月3日 上午10:13:43
     * @param address
     * @return 
     */
    Connection connect(HostAddress address);

    /**
     * 连接器关闭
     * @author keyhunter
     * 2016年6月3日 上午10:21:29
     */
    void shutdown();
}

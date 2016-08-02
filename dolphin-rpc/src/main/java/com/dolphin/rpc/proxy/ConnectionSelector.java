package com.dolphin.rpc.proxy;

import com.dolphin.rpc.core.io.Connection;

/**
 * 连接选择器
 * @author jiujie
 * @version $Id: ConnectorLoadBanlance.java, v 0.1 2016年5月31日 下午1:35:56 jiujie Exp $
 */
public interface ConnectionSelector {

    /**
     * 通过服务分组和服务名字选择一个连接
     * @author jiujie
     * 2016年7月25日 上午11:55:14
     * @param group
     * @param serviceName
     * @return
     */
    Connection select(String group, String serviceName);

}

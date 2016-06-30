package com.dolphin.rpc.proxy;

import com.dolphin.rpc.core.io.Connection;

/**
 * 连接选择器
 * @author jiujie
 * @version $Id: ConnectorLoadBanlance.java, v 0.1 2016年5月31日 下午1:35:56 jiujie Exp $
 */
public interface ConnectionSelector {

    Connection select(String group, String serviceName);

}

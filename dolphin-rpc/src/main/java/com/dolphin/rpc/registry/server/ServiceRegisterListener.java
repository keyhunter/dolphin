package com.dolphin.rpc.registry.server;

import com.dolphin.rpc.core.ApplicationType;
import com.dolphin.rpc.registry.ServiceInfo;

/**
 * 服务注册监听器
 * @author jiujie
 * @version $Id: RegistryServerAddressListener.java, v 0.1 2016年6月1日 下午7:15:10 jiujie Exp $
 */
public interface ServiceRegisterListener {

    void register(ApplicationType applicationType, ServiceInfo serviceInfo);

    void unRegister(ApplicationType applicationType, ServiceInfo serviceInfo);

}

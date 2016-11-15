package com.dolphin.registry.netty.server;

import com.dolphin.core.ApplicationType;
import com.dolphin.core.protocle.transport.ServiceInfo;

/**
 * 服务注册监听器
 * @author jiujie
 * @version $Id: RegistryServerAddressListener.java, v 0.1 2016年6月1日 下午7:15:10 jiujie Exp $
 */
public interface ServiceRegisterListener {

    void register(ApplicationType applicationType, ServiceInfo serviceInfo);

    void unRegister(ApplicationType applicationType, ServiceInfo serviceInfo);

}

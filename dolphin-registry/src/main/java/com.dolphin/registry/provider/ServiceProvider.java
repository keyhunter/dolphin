package com.dolphin.registry.provider;

import com.dolphin.core.protocle.transport.ServiceInfo;

public interface ServiceProvider {

    /**
     * 注册服务
     *
     * @param serviceInfo
     * @author tianxiao
     * @version 2016年7月19日 下午1:42:13 tianxiao
     */
    void register(ServiceInfo serviceInfo);

    /**
     * 注销服务
     *
     * @param serviceInfo
     * @author tianxiao
     * @version 2016年7月19日 下午1:42:22 tianxiao
     */
    void unRegister(ServiceInfo serviceInfo);

}

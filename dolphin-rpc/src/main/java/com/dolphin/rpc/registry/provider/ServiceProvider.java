package com.dolphin.rpc.registry.provider;

import com.dolphin.rpc.registry.ServiceInfo;

public interface ServiceProvider {

    void register(ServiceInfo serviceInfo);

    void unRegister(ServiceInfo serviceInfo);

}

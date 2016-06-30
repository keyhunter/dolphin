package com.dolphin.rpc.registry;

public interface ServiceListener {

    void register(ServiceInfo serviceInfo);

    void unRegister(ServiceInfo serviceInfo);

}

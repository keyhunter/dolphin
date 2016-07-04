package com.dolphin.rpc.registry.consumer;

import com.dolphin.rpc.registry.ServiceInfo;

public interface ServiceCustomer {

    ServiceInfo[] getServices(String group, String serviceName);

    void subcride(String group, String serviceName);

    void unSubcride(String group, String serviceName);

}

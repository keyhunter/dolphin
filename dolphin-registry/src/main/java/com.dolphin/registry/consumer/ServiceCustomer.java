package com.dolphin.registry.consumer;

import com.dolphin.core.protocle.transport.ServiceInfo;

public interface ServiceCustomer {

    ServiceInfo[] getServices(String group, String serviceName);

    void subcride(String group, String serviceName);

    void unSubcride(String group, String serviceName);

}

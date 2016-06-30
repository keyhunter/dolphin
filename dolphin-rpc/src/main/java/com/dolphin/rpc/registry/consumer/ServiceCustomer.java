package com.dolphin.rpc.registry.consumer;

import com.dolphin.rpc.registry.ServiceInfo;
import com.dolphin.rpc.registry.ServiceListener;

public interface ServiceCustomer extends ServiceListener {

    ServiceInfo[] getServices(String group, String serviceName);

    void subcride(String group, String serviceName);

    void unSubcride(String group, String serviceName);

}

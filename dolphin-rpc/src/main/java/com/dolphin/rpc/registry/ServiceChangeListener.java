package com.dolphin.rpc.registry;

public interface ServiceChangeListener {

    void change(String group, String serviceName);


}

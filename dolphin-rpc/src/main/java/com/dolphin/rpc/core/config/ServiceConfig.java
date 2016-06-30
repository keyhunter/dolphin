package com.dolphin.rpc.core.config;

public class ServiceConfig extends DolphinConfig {

    private String serviceName;
    private String group;

    public ServiceConfig() {
        super();
        this.serviceName = getString("/dolphin/service/name");
        this.group = getString("/dolphin/service/group");
    }

    public String getGroup() {
        return group;
    }

    public String getServiceName() {
        return serviceName;
    }

}

package com.dolphin.core.protocle.transport;

import com.dolphin.core.ApplicationType;
import com.dolphin.core.protocle.Request;

public class RegistryRequest implements Request {

    private long id;

    private String command;

    private ApplicationType applicationType;

    private ServiceInfo serviceInfo;

    public RegistryRequest() {
    }

    public RegistryRequest(ApplicationType applicationType, String command,
                           ServiceInfo serviceInfo) {
        this.applicationType = applicationType;
        this.command = command;
        this.serviceInfo = serviceInfo;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(ApplicationType applicationType) {
        this.applicationType = applicationType;
    }

}

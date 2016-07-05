package com.dolphin.rpc.registry.provider;

import org.apache.log4j.Logger;

import com.dolphin.rpc.registry.ServiceInfo;

public abstract class AbstractServiceProvider implements ServiceProvider {

    private ServiceInfo serviceInfo;

    private Logger      logger = Logger.getLogger(AbstractServiceProvider.class);

    public AbstractServiceProvider(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public void registerSelf() {
        if (logger.isInfoEnabled()) {
            logger.info("Register self: [" + serviceInfo.toString() + "]");
        }
        register(serviceInfo);
    }

}

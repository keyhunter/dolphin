package com.dolphin.registry.provider;

import com.dolphin.core.protocle.transport.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServiceProvider implements ServiceProvider {

    private ServiceInfo serviceInfo;

    private Logger logger = LoggerFactory.getLogger(AbstractServiceProvider.class);

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

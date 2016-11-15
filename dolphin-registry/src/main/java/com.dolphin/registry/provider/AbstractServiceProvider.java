package com.dolphin.registry.provider;

import com.dolphin.core.protocle.transport.ServiceInfo;
import org.apache.log4j.Logger;

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

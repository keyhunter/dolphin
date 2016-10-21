package com.dolphin.rpc.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DolphinConfig extends AbstractXMLConfig {

    private static final String DEFAUT_PATH = "dolphin.xml";

    private static final Logger LOGGER      = LoggerFactory.getLogger(DolphinConfig.class);

    public DolphinConfig() {
        super(DEFAUT_PATH);
    }

    protected Logger getLog() {
        return LOGGER;
    }

}

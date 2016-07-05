package com.dolphin.rpc.core.io;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractConnector implements Connector {

    private ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    
    
}

package com.dolphin.rpc.server;

import com.dolphin.rpc.core.config.ServiceConfig;
import com.dolphin.rpc.core.utils.HostUtil;
import com.showjoy.core.BaseJunitRunner;

public class ServerBoot {

    private volatile boolean isBooted = false;

    public void start() {
        if (BaseJunitRunner.isRunWithJunit()) {
            return;
        }
        ServiceConfig serviceConfig = new ServiceConfig();
        int[] ports = serviceConfig.getPorts();
        for (int port : ports) {
            if (HostUtil.isPortBound(port)) {
                continue;
            }
            startServer(port);
            return;
        }
    }

    private synchronized void startServer(int port) {
        if (isBooted) {
            return;
        }
        RPCServer rpcServer = new RPCServer(port);
        rpcServer.startup();
        isBooted = true;
    }

}

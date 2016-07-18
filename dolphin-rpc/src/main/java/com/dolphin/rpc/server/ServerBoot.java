package com.dolphin.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dolphin.rpc.core.config.ServiceConfig;
import com.dolphin.rpc.core.exception.RPCRunTimeException;
import com.dolphin.rpc.core.utils.HostUtil;
import com.showjoy.core.BaseJunitRunner;

public class ServerBoot {

    private volatile boolean isBooted = false;

    private Logger           logger   = LoggerFactory.getLogger(ServerBoot.class);

    public void start() {
        if (BaseJunitRunner.isRunWithJunit()) {
            return;
        }
        ServiceConfig serviceConfig = ServiceConfig.getInstance();
        int[] ports = serviceConfig.getPorts();
        for (int port : ports) {
            if (HostUtil.isPortBound(port)) {
                continue;
            }
            startServer(port);
            return;
        }
        //启动失败加入日志打印，异常抛出
        logger.error("Service start faild, all port has been used.");
        throw new RPCRunTimeException("Service start faild, all port has been used.");
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

package com.dolphin.server.invocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dolphin.core.config.ServiceConfig;
import com.dolphin.core.exception.RPCRunTimeException;
import com.dolphin.core.utils.HostUtil;

public class ServerBoot {

    private volatile boolean isBooted = false;

    private Logger           logger   = LoggerFactory.getLogger(ServerBoot.class);

    public void start() {
        //        if (BaseJunitRunner.isRunWithJunit()) {
        //            return;
        //        }
        ServiceConfig serviceConfig = ServiceConfig.getInstance();
        boolean isPreview = isPreview();
        int[] ports = null;
        if (isPreview) {
            ports = serviceConfig.getPreviewPorts();
        } else {
            ports = serviceConfig.getPorts();
        }
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

    /**
     * 是否是预发环境
     * @author jiujie
     * 2016年7月27日 下午8:22:47
     * @return
     */
    private boolean isPreview() {
        boolean isPreview = false;
        String serverId = System.getProperty("serverId");
        if (serverId != null && serverId.contains("preview")) {
            isPreview = true;
        }
        return isPreview;
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

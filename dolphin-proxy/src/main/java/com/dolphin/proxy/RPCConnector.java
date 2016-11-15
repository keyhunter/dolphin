package com.dolphin.proxy;

import com.dolphin.netty.connector.NettyConnector;

/**
 * RPC客户端
 *
 * @author jiujie
 * @version $Id: RPCClient.java, v 0.1 2016年5月23日 下午2:40:47 jiujie Exp $
 */
public class RPCConnector extends NettyConnector {

    public RPCConnector() {
        super();
        //        registerHandler("heartBeatClientHandler", new HeartBeatClientHandler());
        //        registerHandler("timeoutHandler", new ReadTimeoutHandler(20));
    }

}

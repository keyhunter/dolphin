package com.dolphin.core.exception;

/**
 * 没有该RPC服务异常
 *
 * @author keyhunter
 * @version $Id: RPCServiceNotFoundException.java, v 0.1 2016年5月11日 上午11:03:36 keyhunter Exp $
 */
public class ServiceNotFoundException extends RPCRunTimeException {

    /**
     * @author keyhunter 2016年5月11日 上午11:03:29
     */
    private static final long serialVersionUID = 1L;

    public ServiceNotFoundException() {
        super("RPC service not found.");
    }

}

package com.dolphin.rpc.core.exception;

public class InvokeTimeoutException extends RPCRunTimeException {

    /** @author jiujie 2016年5月12日 下午4:18:06 */
    private static final long serialVersionUID = 1L;

    public InvokeTimeoutException() {
        super("Invoke the method time out.");
    }

}

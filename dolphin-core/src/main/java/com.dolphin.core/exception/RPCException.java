package com.dolphin.rpc.core.exception;

/**
 * RPC异常
 * @author jiujie
 * @version $Id: RPCException.java, v 0.1 2016年5月9日 下午5:35:52 jiujie Exp $
 */
public class RPCException extends Exception {

    /**  */
    private static final long serialVersionUID = -1105695927225940250L;

    private String            errorCode;

    public RPCException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode + "";

    }

    public RPCException(String errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;

    }

    public RPCException(String msg) {
        super(msg);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}

package com.dolphin.core.protocle.transport;

import com.dolphin.core.protocle.Response;

/**
 * RPC结果
 * @author jiujie
 * @version $Id: RPCResult.java, v 0.1 2016年5月23日 下午4:05:59 jiujie Exp $
 */
public class RPCResult implements Response {

    private long      requestId;

    private Object    result;

    private Exception exception;

    public RPCResult() {
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

}

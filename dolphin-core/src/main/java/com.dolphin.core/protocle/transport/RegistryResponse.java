package com.dolphin.core.protocle.transport;

import com.dolphin.core.protocle.Response;

public class RegistryResponse implements Response {

    private String    command;

    private long      requestId;

    private Object    result;

    private Exception exception;

    public RegistryResponse() {
    }

    public RegistryResponse(String command, Object result) {
        this.command = command;
        this.result = result;
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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

}

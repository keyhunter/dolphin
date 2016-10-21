package com.dolphin.rpc.core.io;

public interface Response {

    long getRequestId();

    Object getResult();

    Exception getException();

}

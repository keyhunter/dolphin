package com.dolphin.core.protocle;

public interface Response {

    long getRequestId();

    Object getResult();

    Exception getException();

}

package com.dolphin.rpc.core.io.request;

import com.dolphin.rpc.core.io.Response;

public interface Callback {

    void handle(Response response);

}

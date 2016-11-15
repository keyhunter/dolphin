package com.dolphin.core.protocle.request;

import com.dolphin.core.protocle.Response;

public interface Callback {

    void handle(Response response);

}

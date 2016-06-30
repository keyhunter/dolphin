package com.dolphin.rpc.netty;

import com.dolphin.rpc.core.io.Request;

public class HeartBeat implements Request{

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public void setId(long id) {
    }

}

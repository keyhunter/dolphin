package com.dolphin.netty;

import com.dolphin.core.protocle.Request;

public class HeartBeat implements Request{

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public void setId(long id) {
    }

}

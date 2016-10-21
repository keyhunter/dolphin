package com.dolphin.rpc.core.io;

import com.dolphin.rpc.core.io.transport.Message;

public interface MessageReadListener {

    void read(Message message);

}

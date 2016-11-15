package com.dolphin.core.protocle;

import com.dolphin.core.protocle.transport.Message;

public interface MessageReadListener {

    void read(Message message);

}

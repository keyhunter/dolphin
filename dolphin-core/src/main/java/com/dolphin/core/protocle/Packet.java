package com.dolphin.core.protocle;

import com.dolphin.core.protocle.transport.Header;

public interface Packet {

    Header getHeader();

    byte[] getBody();

    <T> T getBody(Class<T> clazz);
}

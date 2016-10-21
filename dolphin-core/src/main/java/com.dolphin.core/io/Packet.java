package com.dolphin.rpc.core.io;

import com.dolphin.rpc.core.io.transport.Header;

public interface Packet {

    Header getHeader();

    byte[] getBody();

    <T> T getBody(Class<T> clazz);
}

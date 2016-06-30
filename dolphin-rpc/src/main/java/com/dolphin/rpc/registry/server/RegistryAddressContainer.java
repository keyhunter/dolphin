package com.dolphin.rpc.registry.server;

import java.util.List;

import com.dolphin.rpc.core.io.HostAddress;

public interface RegistryAddressContainer {

    List<HostAddress> getAll();

    void add(HostAddress address);

    void remove(HostAddress address);

}

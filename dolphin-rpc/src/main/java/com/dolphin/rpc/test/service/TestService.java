package com.dolphin.rpc.test.service;

import com.dolphin.rpc.core.annotation.RPCService;
import com.dolphin.rpc.test.Constants;
import com.dolphin.rpc.test.Product;

@RPCService(group = "test", value = Constants.SERVICE_NAME)
public interface TestService {

    int createOrder(String name, Product[] products);

    void test();

}

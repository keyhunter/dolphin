package com.dolphin.test.service;

import com.dolphin.core.annotation.RPCService;
import com.dolphin.test.Constants;
import com.dolphin.test.Product;

@RPCService(group = "test", value = Constants.SERVICE_NAME)
public interface TestService {

    int createOrder(String name, Product[] products);

    void test();

}

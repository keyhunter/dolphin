package com.dolphin.test.proxy;

import com.dolphin.proxy.RPCFactory;
import com.dolphin.test.service.TestService;

public class ProxyTest {

    public static void main(String[] args) {
        TestService service = RPCFactory.getService(TestService.class);
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            service.test();
            int createOrder = service.createOrder("sdfsdf", null);
            System.out.println(i + ":" + createOrder);
        }
        System.out.println(System.currentTimeMillis() - currentTimeMillis);
    }

}

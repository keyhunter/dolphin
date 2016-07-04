package com.dolphin.rpc.test.proxy;

import com.dolphin.rpc.proxy.RPCFactory;
import com.dolphin.rpc.test.service.OrderService;

public class ProxyTest {

    public static void main(String[] args) {
        OrderService service = RPCFactory.getService(OrderService.class);
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            service.test();
            int createOrder = service.createOrder("sdfsdf", null);
            System.out.println(i + ":" + createOrder);
        }
        System.out.println(System.currentTimeMillis() - currentTimeMillis);
    }

}

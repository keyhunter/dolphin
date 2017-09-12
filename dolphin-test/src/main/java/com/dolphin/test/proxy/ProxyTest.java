package com.dolphin.test.proxy;

import com.dolphin.proxy.RPCFactory;
import com.dolphin.test.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyTest {

    private static final Logger logger = LoggerFactory.getLogger(ProxyTest.class);


    public static void main(String[] args) {
        TestService service = RPCFactory.getService(TestService.class);
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            service.test();
            int createOrder = service.createOrder("sdfsdf", null);
            logger.info(i + ":" + createOrder);
        }
    }

}

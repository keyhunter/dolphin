package com.dolphin.test.service;

import com.dolphin.test.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service("testService")
public class TestServiceImp implements TestService {

    private static final Logger logger = LoggerFactory.getLogger(TestServiceImp.class);

    private static AtomicLong al = new AtomicLong();

    public TestServiceImp() {
    }

    @Override
    public int createOrder(String name, Product[] products) {
        logger.info(al.incrementAndGet() + "");
        logger.info(name);
        if (products == null) {
            return 0;
        }
        for (Product p : products) {
            logger.info(p.getId() + p.getName());
        }
        return 0;
    }

    @Override
    public void test() {
        logger.info(al.incrementAndGet() + "");
        logger.info("I'm running....................");
    }

}

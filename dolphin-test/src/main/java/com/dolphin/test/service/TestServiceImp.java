package com.dolphin.test.service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.dolphin.test.Product;

@Service("testService")
public class TestServiceImp implements TestService {

    private static AtomicLong al = new AtomicLong();

    public TestServiceImp() {
    }

    @Override
    public int createOrder(String name, Product[] products) {
        System.out.println(al.incrementAndGet());
        System.out.println(name);
        if (products == null) {
            return 0;
        }
        for (Product p : products) {
            System.out.println(p.getId() + p.getName());
        }
        return 0;
    }

    @Override
    public void test() {
        System.out.println(al.incrementAndGet());
        System.out.println("I'm running....................");
    }

}

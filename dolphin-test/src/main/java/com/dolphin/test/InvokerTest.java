package com.dolphin.test;

import com.dolphin.core.invocation.Invoker;
import com.dolphin.server.invocation.ScannerInvoker;

import java.lang.reflect.InvocationTargetException;


public class InvokerTest {

    public static void main(String[] args) throws InvocationTargetException {
        Invoker invoker = new ScannerInvoker();
        Object[] parameters = new Object[2];
        parameters[0] = "贱人";
        parameters[1] = new Product[]{new Product(1, "香水"), new Product(2, "汽车"),
                new Product(4, "汽车"), new Product(2, "汽车"),
                new Product(2, "汽车"), new Product(2, "汽车"),
                new Product(2, "汽车"), new Product(2, "汽车"),
                new Product(4, "汽车"), new Product(2, "汽车"),
                new Product(2, "汽车"), new Product(2, "汽车"),
                new Product(2, "汽车"), new Product(2, "汽车"),
                new Product(4, "汽车"), new Product(2, "汽车"),
                new Product(2, "汽车"), new Product(2, "汽车"),
                new Product(2, "汽车"), new Product(2, "汽车"),
                new Product(4, "汽车"), new Product(2, "汽车"),
                new Product(2, "汽车"), new Product(2, "汽车"),
                new Product(2, "汽车"), new Product(2, "汽车"),
                new Product(4, "汽车"), new Product(2, "汽车"),
                new Product(2, "汽车"), new Product(2, "汽车"),
                new Product(2, "汽车"), new Product(2, "汽车"),
                new Product(4, "汽车"), new Product(2, "汽车"),
                new Product(2, "汽车"), new Product(2, "汽车"),
                new Product(2, "汽车"), new Product(2, "汽车")};
        invoker.invoke("com.dolphin.test.service.TestService", "createOrder", parameters,
                new Class<?>[]{String.class, Product[].class});
        long currentTimeMillis = System.currentTimeMillis();
        System.out.println(currentTimeMillis);
        for (int i = 0; i < 1000000; i++) {
            getClasses(parameters);
        }
        System.out.println(System.currentTimeMillis() - currentTimeMillis);

    }

    protected static Class<?>[] getClasses(Object[] parameters) {
        if (parameters == null) {
            return null;
        }
        Class<?>[] classes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            classes[i] = parameters[i].getClass();
        }
        return classes;
    }
}

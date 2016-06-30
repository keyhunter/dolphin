package com.dolphin.rpc.server.invocation.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContextsUtil {

    private static ApplicationContext context = new ClassPathXmlApplicationContext(
        "classpath*:/spring/root-context.xml");

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static Object getBean(Class<?> clazz) {
        return context.getBean(clazz);
    }

}

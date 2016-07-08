package com.dolphin.rpc.spring;

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.dolphin.rpc.core.exception.ServiceNotFoundException;
import com.dolphin.rpc.server.AbstractInvoker;

/**
 * 基于Spring的方法调用
 * @author jiujie
 * @version $Id: SpringInvoker.java, v 0.1 2016年5月11日 上午11:05:22 jiujie Exp $
 */
@Component
public class SpringInvoker extends AbstractInvoker implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public SpringInvoker() {
        //        this.applicationContext = new ClassPathXmlApplicationContext(
        //            "classpath*:/spring/root-context.xml");
    }

    @Override
    public Object invoke(String className, String methodName, Object[] parameters,
                         Class<?>[] parameterTypes) {
        Object bean;
        Class<?> beanClass;
        try {
            beanClass = Class.forName(className);
            bean = applicationContext.getBean(beanClass);
        } catch (ClassNotFoundException e) {
            throw new ServiceNotFoundException();
        }
        Method method = ReflectionUtils.findMethod(beanClass, methodName, parameterTypes);
        return ReflectionUtils.invokeMethod(method, bean, parameters);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringInvoker.applicationContext = applicationContext;
    }
}

package com.dolphin.spring;

import java.lang.reflect.Field;

import com.dolphin.proxy.RPCFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;


/**
 * spring初始化Bean时注入远程资源类
 *
 * @author wuque
 * @version $Id: RemotingBeanPostProcessor.java, v 0.1 2016年5月26日 下午2:34:12 wuque Exp $
 */
@Component
public class RemotingBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {

    @Override
    public Object postProcessBeforeInitialization(Object bean,
                                                  String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(RPCResource.class)) {
                RPCResource annotation = field.getAnnotation(RPCResource.class);
                field.setAccessible(true);
                Object service = null;
                String name = annotation.name();
                if (StringUtils.isBlank(name)) {
                    service = RPCFactory.getService(field.getType());
                } else {
                    service = RPCFactory.getService(field.getType(), name);
                }
                try {
                    field.set(bean, service);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean,
                                                 String beanName) throws BeansException {
        return bean;
    }

    @Override
    public int getOrder() {
        return -1;
    }

}

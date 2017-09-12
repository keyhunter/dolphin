package com.dolphin.spring;

import com.dolphin.proxy.RPCFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;


/**
 * spring初始化Bean时注入远程资源类
 *
 * @author wuque
 * @version $Id: RemotingBeanPostProcessor.java, v 0.1 2016年5月26日 下午2:34:12 wuque Exp $
 */
@Component
public class RemotingBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {

    private static final Logger logger = LoggerFactory.getLogger(RemotingBeanPostProcessor.class);

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
                    logger.error("set bean service error", e);
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

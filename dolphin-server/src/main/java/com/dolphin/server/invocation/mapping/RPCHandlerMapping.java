package com.dolphin.server.invocation.mapping;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.dolphin.core.annotation.RPCService;

/**
 * RPC服务Service名字的映射
 * @author jiujie
 * @version $Id: ServiceMapping.java, v 0.1 2016年5月23日 下午7:47:40 jiujie Exp $
 */
public class RPCHandlerMapping {

    private Map<String, Object>               handlers;

    private static volatile RPCHandlerMapping handlerMapping;

    private RPCHandlerMapping() {
        handlers = new HashMap<>();
    }

    public static RPCHandlerMapping getInstance() {
        if (handlerMapping == null) {
            synchronized (RPCHandlerMapping.class) {
                if (handlerMapping == null) {
                    handlerMapping = new RPCHandlerMapping();
                }
            }
        }
        return handlerMapping;
    }

    public void register(Object object) {
        Class<?> clazz = object.getClass();
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces != null) {
            for (Class<?> interfaceClass : interfaces) {
                if (interfaceClass.getAnnotation(RPCService.class) != null) {
                    register(clazz.getName(), object);
                }
            }
        }
        handlers.put(clazz.getName(), object);
    }

    public void register(String name, Object object) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        Object service = handlers.get(name);
        if (service == null) {
            synchronized (service) {
                if (handlers.get(name) == null) {
                    handlers.put(name, object);
                }
            }
        }
    }

    public Object getHandler(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return handlers.get(name);
    }

}

package com.dolphin.rpc.server.invocation.scanner;

import java.lang.annotation.Annotation;

import com.dolphin.rpc.core.annotation.RPCService;
import com.dolphin.rpc.core.utils.ClassFilter;

public class RpcServiceClassFilter implements ClassFilter {

    @Override
    public boolean accept(Class clazz) {
        if (clazz.isInterface()) {
            return false;
        }
        Class[] interfaces = clazz.getInterfaces();
        if (interfaces == null) {
            return false;
        }
        Annotation annotation = clazz.getAnnotation(RPCService.class);
        if (annotation != null) {
            return true;
        }
        for (Class<?> interfaceClass : interfaces) {
            if (interfaceClass.getAnnotation(RPCService.class) == null) {
                continue;
            }
            return true;
        }
        return false;
    }

}

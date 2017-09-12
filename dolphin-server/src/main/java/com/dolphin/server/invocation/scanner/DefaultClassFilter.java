package com.dolphin.server.invocation.scanner;

import com.dolphin.core.annotation.RPCService;
import com.dolphin.core.utils.ClassFilter;

import java.lang.annotation.Annotation;

public class DefaultClassFilter implements ClassFilter {

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

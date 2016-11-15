package com.dolphin.server.invocation;

import java.lang.reflect.InvocationTargetException;

import com.dolphin.core.invocation.AbstractInvoker;
import com.dolphin.server.invocation.scanner.RpcServiceScanner;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

public class ScannerInvoker extends AbstractInvoker {

    @Override
    public Object invoke(String className, String methodName, Object[] parameters,
                         Class<?>[] parameterTypes) throws InvocationTargetException {
//        Class<?>[] classes = getClasses(parameters);
        RpcServiceScanner instance = RpcServiceScanner.getInstance();
        Object bean = instance.getBean(className);
        FastClass target = FastClass.create(bean.getClass());
        FastMethod serviceFastMethod = target.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(bean, parameters);
    }

    @Override
    public Object invoke(String className, String implementName, String methodName,
                         Object[] parameters,
                         Class<?>[] parameterTypes) throws InvocationTargetException {
        return invoke(className, methodName, parameters, parameterTypes);
    }

}

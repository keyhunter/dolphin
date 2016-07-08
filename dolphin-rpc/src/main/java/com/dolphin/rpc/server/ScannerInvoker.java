package com.dolphin.rpc.server;

import java.lang.reflect.InvocationTargetException;

import com.dolphin.rpc.server.invocation.scanner.RpcServiceScanner;

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

}

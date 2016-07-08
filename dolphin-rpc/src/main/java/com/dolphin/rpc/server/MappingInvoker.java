package com.dolphin.rpc.server;

import java.lang.reflect.InvocationTargetException;

import com.dolphin.rpc.server.invocation.mapping.RPCHandlerMapping;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

public class MappingInvoker extends AbstractInvoker {

    @Override
    public Object invoke(String className, String methodName, Object[] parameters,
                         Class<?>[] parameterTypes) throws InvocationTargetException {

//        Class<?>[] classes = getClasses(parameters);
        Object bean = RPCHandlerMapping.getInstance().getHandler(className);
        FastClass target = FastClass.create(bean.getClass());
        FastMethod serviceFastMethod = target.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(bean, parameters);
    }

}

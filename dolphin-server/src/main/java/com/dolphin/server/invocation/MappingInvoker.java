package com.dolphin.server.invocation;

import com.dolphin.core.invocation.AbstractInvoker;
import com.dolphin.server.invocation.mapping.RPCHandlerMapping;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;

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

    @Override
    public Object invoke(String className, String implementName, String methodName,
                         Object[] parameters,
                         Class<?>[] parameterTypes) throws InvocationTargetException {
        return invoke(className, methodName, parameters, parameterTypes);
    }

}

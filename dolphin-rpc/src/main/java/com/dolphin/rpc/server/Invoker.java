package com.dolphin.rpc.server;

import java.lang.reflect.InvocationTargetException;

public interface Invoker {

    Object invoke(String className, String methodName, Object[] parameters,
                  Class<?>[] parameterTypes) throws InvocationTargetException;

}

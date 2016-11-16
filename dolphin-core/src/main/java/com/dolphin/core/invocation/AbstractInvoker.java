package com.dolphin.core.invocation;

public abstract class AbstractInvoker implements Invoker {

    protected Class<?>[] getClasses(Object[] parameters) {
        if (parameters == null) {
            return null;
        }
        Class<?>[] classes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];
            classes[i] = parameter == null ? null : parameter.getClass();
        }
        return classes;
    }

    //检测目标是否是RPC服务
    protected void checkTargetClass() {

    }

}

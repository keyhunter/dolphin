package com.dolphin.core.invocation;

import java.lang.reflect.InvocationTargetException;

public interface Invoker {

    /**
     * 当接口有一个实现时，执行方法
     * @author jiujie
     * 2016年7月12日 上午10:37:33
     * @param className
     * @param methodName
     * @param parameters
     * @param parameterTypes
     * @return
     * @throws InvocationTargetException
     */
    Object invoke(String className, String methodName, Object[] parameters,
                  Class<?>[] parameterTypes) throws InvocationTargetException;

    /**
     * 加入implementName参数，用于多实现情况下找出实现类
     * @author jiujie
     * 2016年7月12日 上午10:36:59
     * @param className
     * @param implementName
     * @param methodName
     * @param parameters
     * @param parameterTypes
     * @return
     * @throws InvocationTargetException
     */
    Object invoke(String className, String implementName, String methodName, Object[] parameters,
                  Class<?>[] parameterTypes) throws InvocationTargetException;

}

package com.dolphin.proxy;

import java.lang.reflect.Method;

public interface Filter {

    /**
     * RPC拦截器
     * @author jiujie
     * 2016年6月2日 下午7:35:20
     * @param group
     * @param serviceName
     * @param method
     * @param args
     */
    void invoke(String group, String serviceName, Method method, Object[] args);

}

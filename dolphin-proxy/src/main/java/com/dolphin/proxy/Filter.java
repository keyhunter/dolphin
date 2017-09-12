package com.dolphin.proxy;

import java.lang.reflect.Method;

public interface Filter {

    /**
     * RPC拦截器
     *
     * @param group
     * @param serviceName
     * @param method
     * @param args
     * @author keyhunter
     * 2016年6月2日 下午7:35:20
     */
    void invoke(String group, String serviceName, Method method, Object[] args);

}

package com.dolphin.rpc.proxy;

import java.lang.reflect.Proxy;

public class RPCFactory {

    /**
     * 获取代理类
     * @author jiujie
     * 2016年5月24日 上午11:38:00
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> clazz) {
        RPCServiceProxy rpcServiceProxy = new RPCServiceProxy();
        Object newProxyInstance = Proxy.newProxyInstance(clazz.getClassLoader(),
            new Class[] { clazz }, rpcServiceProxy);
        return (T) newProxyInstance;
    }

}

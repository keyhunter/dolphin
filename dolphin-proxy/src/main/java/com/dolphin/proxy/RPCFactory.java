package com.dolphin.proxy;

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

    /**
     * 通过接口的类型，还有实现类的名字来，得到代理类
     * @author jiujie
     * 2016年7月12日 上午10:48:17
     * @param clazz
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> clazz, String name) {
        RPCServiceProxy rpcServiceProxy = new RPCServiceProxy(name);
        Object newProxyInstance = Proxy.newProxyInstance(clazz.getClassLoader(),
            new Class[] { clazz }, rpcServiceProxy);
        return (T) newProxyInstance;
    }

}

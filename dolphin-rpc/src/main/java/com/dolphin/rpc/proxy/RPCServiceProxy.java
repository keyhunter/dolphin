package com.dolphin.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.management.ServiceNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.dolphin.rpc.core.annotation.RPCService;
import com.dolphin.rpc.core.config.ServiceConfig;
import com.dolphin.rpc.core.io.Connection;
import com.dolphin.rpc.core.io.request.RequestManager;
import com.dolphin.rpc.core.io.transport.Header;
import com.dolphin.rpc.core.io.transport.PacketType;
import com.dolphin.rpc.core.io.transport.RPCRequest;
import com.dolphin.rpc.core.io.transport.RPCResult;

/**
 * RPC服务的代理类，通过该代理，去调用远程的服务。
 * 会根据RPCService注解上的value去服务中心取到该服务所在的地址，并进行通信。
 * @author jiujie
 * @version $Id: RPCServiceProxy.java, v 0.1 2016年6月30日 上午9:56:53 jiujie Exp $
 */
public class RPCServiceProxy implements InvocationHandler {

    private static Logger               logger          = Logger.getLogger(RPCServiceProxy.class);

    private final static RequestManager REQUSET_MANAGER = RequestManager.getInstance();

    private final static String         DEFAULT_GROUP   = new ServiceConfig().getGroup();

    /** 客户端选择器 @author jiujie 2016年5月24日 上午11:33:08 */
    private static ConnectionSelector   clientSelector  = ServiceConnectionSelector.getInstance();

    public RPCServiceProxy() {
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> clazz = method.getDeclaringClass();
        RPCService annotation = clazz.getAnnotation(RPCService.class);
        String serviceName = annotation.value();
        String annotationGroup = annotation.group();
        String group = DEFAULT_GROUP;
        if (StringUtils.isNotBlank(annotationGroup)) {
            group = annotationGroup;
        }
        String className = clazz.getName();
        logBefore(className, method, args);
        RPCResult result = request(group, serviceName, className, method, args);
        logAfter(className, method, args);
        if (result.getException() != null) {
            throw result.getException();
        }
        return result.getResult();
    }

    /**
     * 请求远程服务获取结果
     * @author jiujie
     * 2016年7月7日 下午4:47:00
     * @param group 分组名字
     * @param serviceName 服务名字
     * @param className 类名
     * @param method 方法
     * @param args 参数
     * @return
     * @throws ServiceNotFoundException
     */
    private RPCResult request(String group, String serviceName, String className, Method method,
                              Object[] args) throws ServiceNotFoundException {
        RPCRequest request = new RPCRequest();
        request.setClassName(className);
        request.setMethodName(method.getName());
        request.setParamters(args);
        request.setParamterTypes(method.getParameterTypes());
        Connection connection = clientSelector.select(group, serviceName);
        if (connection == null) {
            throw new ServiceNotFoundException();
        }
        RPCResult result = (RPCResult) REQUSET_MANAGER.sysnRequest(connection,
            new Header(PacketType.RPC), request);
        return result;
    }

    private void logAfter(String className, Method method, Object[] args) {
        if (logger.isInfoEnabled()) {
            String arguments = arrayToString(args);
            logger.info("Service [className:" + className + ", method:" + method.getName()
                        + ", args:" + arguments + "] invoke success.");
        }
    }

    private void logBefore(String className, Method method, Object[] args) {
        if (logger.isInfoEnabled()) {
            String arguments = arrayToString(args);
            logger.info("Service [className:" + className + ", method:" + method.getName()
                        + ", args:" + arguments + "] invoking.");
        }
    }

    private String arrayToString(Object[] args) {
        String arguments = null;
        if (args == null || args.length == 0) {
            arguments = "";
        } else {
            arguments = Arrays.deepToString(args);
        }
        return arguments;
    }

}

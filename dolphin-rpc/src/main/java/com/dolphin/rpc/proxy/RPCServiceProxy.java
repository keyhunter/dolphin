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

    private static Logger         logger         = Logger.getLogger(RPCServiceProxy.class);

    private static RequestManager requestManager = RequestManager.getInstance();

    private String                group;

    /** 客户端选择器 @author jiujie 2016年5月24日 上午11:33:08 */
    private ConnectionSelector    clientSelector;

    public RPCServiceProxy() {
        this.clientSelector = ServiceConnectionSelector.getInstance();
        ServiceConfig serviceConfig = new ServiceConfig();
        group = serviceConfig.getGroup();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> clazz = method.getDeclaringClass();
        RPCService annotation = clazz.getAnnotation(RPCService.class);
        String serviceName = annotation.value();
        String annotationGroup = annotation.group();
        if (StringUtils.isNotBlank(annotationGroup)) {
            group = annotationGroup;
        }
        String className = clazz.getName();
        logBefore(className, method, args);
        RPCRequest request = new RPCRequest();
        request.setClassName(className);
        request.setMethodName(method.getName());
        request.setParamters(args);
        request.setParamterTypes(method.getParameterTypes());
        Connection connection = clientSelector.select(group, serviceName);
        if (connection == null) {
            throw new ServiceNotFoundException();
        }
        RPCResult result = (RPCResult) requestManager.sysnRequest(connection,
            new Header(PacketType.RPC), request);
        logAfter(className, method, args);
        if (result.getException() != null) {
            throw result.getException();
        }
        return result.getResult();
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

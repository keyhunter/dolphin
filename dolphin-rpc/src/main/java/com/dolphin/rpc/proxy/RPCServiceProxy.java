package com.dolphin.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.management.ServiceNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.dolphin.rpc.core.annotation.RPCService;
import com.dolphin.rpc.core.config.ClientConfig;
import com.dolphin.rpc.core.exception.RPCException;
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

    private final static String         DEFAULT_GROUP;

    /** 默认重试次数 @author jiujie 2016年7月18日 上午11:27:27 */
    private final static int            RETRY_TIMES;

    /** 客户端选择器 @author jiujie 2016年5月24日 上午11:33:08 */
    private static ConnectionSelector   clientSelector  = ServiceConnectionSelector.getInstance();

    /** 接口实现类的名字，当一个远程接口有多个实现时需要有此参数  @author jiujie 2016年7月12日 上午10:45:11 */
    private String                      implementName;

    static {
        ClientConfig clientConfig = new ClientConfig();
        DEFAULT_GROUP = clientConfig.getGlobalGroup();
        int retryTimes = clientConfig.getRetryTimes();
        if (retryTimes > 0) {
            RETRY_TIMES = retryTimes;
        } else {
            RETRY_TIMES = 3;
        }
    }

    public RPCServiceProxy() {
    }

    /** 
     * constructor
     * @author jiujie
     * 2016年7月12日 上午10:46:08
     * @param implementName 接口实现类的名字，当一个远程接口有多个实现时需要有此参数
     */
    public RPCServiceProxy(String implementName) {
        this.implementName = implementName;
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
        if (StringUtils.isNotBlank(implementName)) {
            request.setImplementName(implementName);
        }
        request.setParamters(args);
        request.setParamterTypes(method.getParameterTypes());

        RPCResult result = null;
        //如果失败，则重试请求
        for (int i = 0; i < RETRY_TIMES; i++) {
            try {
                Connection connection = clientSelector.select(group, serviceName);
                if (connection == null) {
                    logger.error("Service [group:" + group + "," + "name:" + serviceName + "]"
                                 + " not found.");
                    throw new ServiceNotFoundException();
                }
                result = sendRequest(connection, request);
            } catch (RPCException e) {
                logger.error("", e);
                logger.info("Now retry request: " + i);
            }
            if (result != null) {
                return result;
            }
        }
        return result;
    }

    /**
     * 发送请求
     * @author jiujie
     * 2016年7月18日 上午11:26:06
     * @param connection
     * @param request
     * @return
     * @throws RPCException
     */
    private RPCResult sendRequest(Connection connection, RPCRequest request) throws RPCException {
        RPCResult result;
        result = (RPCResult) REQUSET_MANAGER.sysnRequest(connection, new Header(PacketType.RPC),
            request);
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

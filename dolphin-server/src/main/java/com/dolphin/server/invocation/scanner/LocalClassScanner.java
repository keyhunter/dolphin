package com.dolphin.server.invocation.scanner;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dolphin.core.annotation.RPCService;
import com.dolphin.core.utils.ClassScanner;

import net.sf.cglib.reflect.FastClass;

/**
 * local class scanner
 * scan class by class name
 *
 * @author keyhunter
 * @version $Id: RpcServiceManager.java, v 0.1 2016年5月23日 下午5:20:50 keyhunter Exp $
 */
public class LocalClassScanner {

    private static Logger logger = Logger.getLogger(DefaultClassFilter.class);

    private static Map<String, Object> rpcServices;

    private static volatile LocalClassScanner scanner = null;

    private LocalClassScanner() {
        rpcServices = new HashMap<>();
        List<Class> classes = ClassScanner.scanPackage("com", new DefaultClassFilter());
        if (classes != null && !classes.isEmpty()) {
            for (Class<?> clazz : classes) {
                try {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    if (interfaces != null && interfaces.length != 0) {
                        for (Class<?> interfaceClass : interfaces) {
                            RPCService annotation = interfaceClass.getAnnotation(RPCService.class);
                            if (annotation != null) {
                                rpcServices.put(interfaceClass.getName(),
                                        FastClass.create(clazz).newInstance());
                            }
                        }
                    } else {
                        FastClass fastClass = FastClass.create(clazz);
                        rpcServices.put(clazz.getName(), fastClass.newInstance());
                    }
                } catch (InvocationTargetException e) {
                    logger.error("", e);
                }
            }
        }
    }

    public static LocalClassScanner getInstance() {
        if (scanner == null) {
            synchronized (LocalClassScanner.class) {
                if (scanner == null) {
                    scanner = new LocalClassScanner();
                }
            }
        }
        return scanner;
    }

    public Object getBean(String className) {
        return rpcServices.get(className);
    }

}

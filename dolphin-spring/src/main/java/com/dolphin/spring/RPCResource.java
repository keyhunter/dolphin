package com.dolphin.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC资源注入注解
 * @author jiujie
 * @version $Id: RPCResource.java, v 0.1 2016年7月7日 下午5:31:29 jiujie Exp $
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RPCResource {

    String name() default "";

}

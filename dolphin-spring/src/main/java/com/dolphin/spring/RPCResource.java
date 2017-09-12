package com.dolphin.spring;

import java.lang.annotation.*;

/**
 * RPC资源注入注解
 *
 * @author keyhunter
 * @version $Id: RPCResource.java, v 0.1 2016年7月7日 下午5:31:29 keyhunter Exp $
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RPCResource {

    String name() default "";

}

package com.dolphin.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RPCService {

    /**
     * Service服务名字
     * @author jiujie
     * 2016年7月7日 下午5:31:16
     * @return
     */
    String value() default ""; 

    /**
     * Service服务分组
     * @author jiujie
     * 2016年7月7日 下午5:31:05
     * @return
     */
    String group() default "";

}

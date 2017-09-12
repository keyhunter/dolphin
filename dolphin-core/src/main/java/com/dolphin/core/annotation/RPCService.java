package com.dolphin.core.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RPCService {

    /**
     * Service服务名字
     *
     * @return
     * @author keyhunter
     * 2016年7月7日 下午5:31:16
     */
    String value() default "";

    /**
     * Service服务分组
     *
     * @return
     * @author keyhunter
     * 2016年7月7日 下午5:31:05
     */
    String group() default "";

}

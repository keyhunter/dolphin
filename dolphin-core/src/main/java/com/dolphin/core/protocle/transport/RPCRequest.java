package com.dolphin.core.protocle.transport;

import com.dolphin.core.protocle.Request;

public class RPCRequest implements Request {

    private long       id;

    /** 类名 @author jiujie 2016年7月12日 上午10:25:56 */
    private String     className;

    /** 实现该接口的类的名字，当一个接口有两个实现的时候，要指定实现类的名字
     * 比如： UserService，的实现有两个userService和detailedUserService，这个时候就需要指定实现类的名字
     *  @author jiujie 2016年7月12日 上午10:27:21 */
    private String     implementName;

    /** 方法名 @author jiujie 2016年7月12日 上午10:26:13 */
    private String     methodName;

    /** 参数 @author jiujie 2016年7月12日 上午10:26:20 */
    private Object[]   paramters;

    /** 参数类型 @author jiujie 2016年7月12日 上午10:26:26 */
    private Class<?>[] paramterTypes;

    public RPCRequest() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParamters() {
        return paramters;
    }

    public void setParamters(Object[] paramters) {
        this.paramters = paramters;
    }

    public Class<?>[] getParamterTypes() {
        return paramterTypes;
    }

    public void setParamterTypes(Class<?>[] paramterTypes) {
        this.paramterTypes = paramterTypes;
    }

    public String getImplementName() {
        return implementName;
    }

    public void setImplementName(String implementName) {
        this.implementName = implementName;
    }


}

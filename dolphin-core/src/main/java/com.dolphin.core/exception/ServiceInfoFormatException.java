package com.dolphin.core.exception;

public class ServiceInfoFormatException extends RPCRunTimeException {

    /** @author jiujie 2016年6月2日 下午8:09:46 */
    private static final long serialVersionUID = 1L;

    public ServiceInfoFormatException() {
        super("地址格式不正确");
    }

}

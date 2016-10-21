package com.dolphin.rpc.core.exception;

public class AddressFormatException extends RPCRunTimeException {

    /** @author jiujie 2016年6月2日 下午8:09:46 */
    private static final long serialVersionUID = 1L;

    public AddressFormatException() {
        super("地址格式不正确");
    }

}

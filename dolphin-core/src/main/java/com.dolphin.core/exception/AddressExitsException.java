package com.dolphin.rpc.core.exception;

public class AddressExitsException extends RPCRunTimeException {

    /** @author jiujie 2016年6月2日 下午8:14:50 */
    private static final long serialVersionUID = 1L;

    public AddressExitsException() {
        super("地址已经存在了");
    }

}

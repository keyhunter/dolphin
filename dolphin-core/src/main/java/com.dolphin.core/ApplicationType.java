package com.dolphin.rpc.core;

/**
 * 应用类型
 * @author jiujie
 * @version $Id: ServerType.java, v 0.1 2016年6月1日 上午10:51:59 jiujie Exp $
 */
public enum ApplicationType {

                             RPC_CLIENT(1), RPC_SERVER(2), REGISTRY_SERVER(3);

    private int value;

    private ApplicationType(int value) {
        this.setValue(value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}

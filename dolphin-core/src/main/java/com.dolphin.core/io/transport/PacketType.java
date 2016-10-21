package com.dolphin.rpc.core.io.transport;

public enum PacketType {

                        HEART_BEAT((short) 1), RPC((short) 2), REGISTRY((short) 3);

    private short value;

    private PacketType(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }

}

package com.dolphin.rpc.core.config;

public class ClientConfig extends DolphinConfig {

    private String globalGroup;

    private int    timeOut;

    public ClientConfig() {
        this.globalGroup = getString("/dolphin/client/global/group");
        this.timeOut = getInt("/dolphin/client/timeout");
        if (timeOut == 0) {
            timeOut = 3;
        }
    }

    public String getGlobalGroup() {
        return globalGroup;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

}

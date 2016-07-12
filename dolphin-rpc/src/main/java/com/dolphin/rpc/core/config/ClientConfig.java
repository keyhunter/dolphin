package com.dolphin.rpc.core.config;

/**
 * RPC客户端配置
 * @author jiujie
 * @version $Id: ClientConfig.java, v 0.1 2016年7月11日 下午4:11:20 jiujie Exp $
 */
public class ClientConfig extends DolphinConfig {

    /** 全局RpcService的分组 @author jiujie 2016年7月11日 下午4:11:34 */
    private String globalGroup;

    /** RPC调用超时时间 @author jiujie 2016年7月11日 下午4:11:54 */
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

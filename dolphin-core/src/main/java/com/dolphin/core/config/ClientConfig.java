package com.dolphin.core.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RPC客户端配置
 * @author jiujie
 * @version $Id: ClientConfig.java, v 0.1 2016年7月11日 下午4:11:20 jiujie Exp $
 */
public class ClientConfig extends DolphinConfig {

    /** 全局RpcService的分组 @author jiujie 2016年7月11日 下午4:11:34 */
    private String                    globalGroup;

    /** RPC请求超时时间 @author jiujie 2016年7月11日 下午4:11:54 */
    private int                       timeOut;

    private int                       retryTimes;

    private final Map<String, String> serviceGroups;

    private static ClientConfig       clientConfig;

    public static ClientConfig getInstance() {
        if (clientConfig == null) {
            synchronized (ClientConfig.class) {
                if (clientConfig == null) {
                    clientConfig = new ClientConfig();
                }
            }
        }
        return clientConfig;
    }

    private ClientConfig() {
        this.globalGroup = getString("/dolphin/client/global/group");
        this.timeOut = getInt("/dolphin/client/timeout");
        if (timeOut <= 0) {
            timeOut = 3000;
        }
        this.retryTimes = getInt("/dolphin/client/retry-times");
        if (retryTimes <= 0) {
            retryTimes = 3;
        }
        List<String> names = getStrings("/dolphin/client/services/service/name");
        List<String> groups = getStrings("/dolphin/client/services/service/group");
        if (names == null || groups == null || names.size() != groups.size()) {
            serviceGroups = null;
        } else {
            serviceGroups = new HashMap<>();
            for (int i = 0; i < names.size(); i++) {
                serviceGroups.put(names.get(i), groups.get(i));
            }
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

    public int getRetryTimes() {
        return retryTimes;
    }

    public Map<String, String> getServiceGroups() {
        return serviceGroups;
    }

}

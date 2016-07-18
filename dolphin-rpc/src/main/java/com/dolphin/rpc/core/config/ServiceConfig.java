package com.dolphin.rpc.core.config;

import java.util.List;

/**
 * RPC服务提供方配置
 * @author jiujie
 * @version $Id: ServiceConfig.java, v 0.1 2016年7月11日 下午4:12:13 jiujie Exp $
 */
public class ServiceConfig extends DolphinConfig {

    /** 服务名称 @author jiujie 2016年7月11日 下午4:12:24 */
    private String               serviceName;
    /** 服务分组 @author jiujie 2016年7月11日 下午4:12:31 */
    private String               group;

    /** 服务ip @author jiujie 2016年7月12日 上午11:09:48 */
    private String               ip;

    /** 服务端口号 @author jiujie 2016年7月12日 上午11:09:28 */
    private int[]                ports;

    private static ServiceConfig serviceConfig;

    public static ServiceConfig getInstance() {
        if (serviceConfig == null) {
            synchronized (ClientConfig.class) {
                if (serviceConfig == null) {
                    serviceConfig = new ServiceConfig();
                }
            }
        }
        return serviceConfig;
    }

    private ServiceConfig() {
        super();
        this.serviceName = getString("/dolphin/service/name");
        this.group = getString("/dolphin/service/group");
        this.ip = getString("/dolphin/service/ip");
        List<String> strings = getStrings("/dolphin/service/ports/port");
        if (strings != null && strings.size() > 0) {
            ports = new int[strings.size()];
            try {
                for (int i = 0; i < strings.size(); i++) {
                    ports[i] = Integer.valueOf(strings.get(i));
                }
            } catch (Exception exception) {
                getLog().error("Is the format of service's port right?");
            }
        }
    }

    public String getGroup() {
        return group;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int[] getPorts() {
        return ports;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}

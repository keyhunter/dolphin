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

    /** 服务ip，支持正则匹配 @author jiujie 2016年7月12日 上午11:09:48 */
    private String               ip;

    /** 服务IP正则，可以通过正则动态获取网卡的网址  @author jiujie 2016年7月28日 下午1:26:58 */
    private String               ipRegex;

    /** 服务端口号 @author jiujie 2016年7月12日 上午11:09:28 */
    private int[]                ports;

    /** 预发环境端口号配置 @author jiujie 2016年7月27日 下午6:52:20 */
    private int[]                previewPorts;

    private static ServiceConfig serviceConfig;

    public static ServiceConfig getInstance() {
        if (serviceConfig == null) {
            synchronized (ServiceConfig.class) {
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
        this.ipRegex = getString("/dolphin/service/ip-regex");
        this.ports = list2IntArr(getStrings("/dolphin/service/ports/port"));
        this.previewPorts = list2IntArr(getStrings("/dolphin/service/ports/preview-port"));
    }

    private int[] list2IntArr(List<String> stringList) {
        if (stringList != null && stringList.size() > 0) {
            int[] intArr = new int[stringList.size()];
            try {
                for (int i = 0; i < stringList.size(); i++) {
                    intArr[i] = Integer.valueOf(stringList.get(i));
                }
            } catch (Exception exception) {
                getLog().error("Is the format of service's port right?");
                return null;
            }
            return intArr;
        }
        return null;
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

    public int[] getPreviewPorts() {
        return previewPorts;
    }

    public String getIpRegex() {
        return ipRegex;
    }

}

package com.dolphin.rpc.core.config;

/**
 * RPC服务提供方配置
 * @author jiujie
 * @version $Id: ServiceConfig.java, v 0.1 2016年7月11日 下午4:12:13 jiujie Exp $
 */
public class ServiceConfig extends DolphinConfig {

    /** 服务名称 @author jiujie 2016年7月11日 下午4:12:24 */
    private String serviceName;
    /** 服务分组 @author jiujie 2016年7月11日 下午4:12:31 */
    private String group;

    public ServiceConfig() {
        super();
        this.serviceName = getString("/dolphin/service/name");
        this.group = getString("/dolphin/service/group");
    }

    public String getGroup() {
        return group;
    }

    public String getServiceName() {
        return serviceName;
    }

}

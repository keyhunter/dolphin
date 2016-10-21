package com.dolphin.rpc.core.config;

/**
 * 注册中心配置
 * @author jiujie
 * @version $Id: RegistryConfig.java, v 0.1 2016年6月2日 下午8:01:57 jiujie Exp $
 */
public class RegistryConfig extends DolphinConfig {

    /** 注册中心地址连接的数据库相关配置 @author jiujie 2016年7月11日 下午4:13:47 */
    private String                dbUrl;
    private String                dbUsername;
    private String                dbPassword;

    /** 服务消费的配置 @author jiujie 2016年7月11日 下午4:14:11 */
    private String                customer;
    /** 服务提供都的配置 @author jiujie 2016年7月11日 下午4:14:29 */
    private String                provider;

    private static RegistryConfig registryConfig;

    public static RegistryConfig getInstance() {
        if (registryConfig == null) {
            synchronized (ClientConfig.class) {
                if (registryConfig == null) {
                    registryConfig = new RegistryConfig();
                }
            }
        }
        return registryConfig;
    }

    private RegistryConfig() {
        super();
        this.dbUrl = getString("/dolphin/registry/datasource/url");
        this.dbUsername = getString("/dolphin/registry/datasource/username");
        this.dbPassword = getString("/dolphin/registry/datasource/password");
        this.customer = getString("/dolphin/registry/customer");
        this.provider = getString("/dolphin/registry/provider");
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getCustomer() {
        return customer;
    }

    public String getProvider() {
        return provider;
    }

}

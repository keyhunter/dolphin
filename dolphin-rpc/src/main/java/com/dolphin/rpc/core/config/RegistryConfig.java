package com.dolphin.rpc.core.config;

/**
 * 注册中心配置
 * @author jiujie
 * @version $Id: RegistryConfig.java, v 0.1 2016年6月2日 下午8:01:57 jiujie Exp $
 */
public class RegistryConfig extends DolphinConfig {

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    public RegistryConfig() {
        super();
        this.dbUrl = getString("/dolphin/registry/datasource/url");
        this.dbUsername = getString("/dolphin/registry/datasource/username");
        this.dbPassword = getString("/dolphin/registry/datasource/password");
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
}

package com.dolphin.core.protocle;

import org.apache.commons.lang.StringUtils;

public class HostAddress {

    /** 主机IP地址  @author jiujie 2016年5月13日 上午11:10:31 */
    private String host;

    /** 主机端口号  @author jiujie 2016年5月13日 上午11:10:50 */
    private int    port;

    public static boolean verify(HostAddress address) {
        if (address == null || StringUtils.isBlank(address.getHost()) || address.getPort() <= 0) {
            return false;
        }
        return true;
    }

    public HostAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HostAddress) {
            HostAddress hostAddress = (HostAddress) obj;
            if (!this.getHost().equals(hostAddress.getHost())) {
                return false;
            }
            if (this.getPort() != hostAddress.getPort()) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "host:" + host + ",port:" + port;
    }

}

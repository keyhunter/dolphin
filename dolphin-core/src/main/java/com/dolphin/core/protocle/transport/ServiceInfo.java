package com.dolphin.core.protocle.transport;


import com.dolphin.core.protocle.HostAddress;

/**
 * 服务信息
 * @author jiujie
 * @version $Id: ServiceInfo.java, v 0.1 2016年5月16日 下午5:45:29 jiujie Exp $
 */
public class ServiceInfo {
    
    /** Service的分组  @author jiujie 2016年5月31日 下午3:16:35 */
    private String      group;

    /** 服务名字 @author jiujie 2016年5月16日 下午5:44:53 */
    private String      name;

    /** 服务地址 @author jiujie 2016年5月16日 下午5:45:06 */
    private HostAddress hostAddress;

    public ServiceInfo() {
    }

    public ServiceInfo(String group, String name, HostAddress address) {
        this.group = group;
        this.name = name;
        this.hostAddress = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HostAddress getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(HostAddress hostAddress) {
        this.hostAddress = hostAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ServiceInfo) {
            ServiceInfo serviceInfo = (ServiceInfo) obj;
            if (!this.getGroup().equals(serviceInfo.getGroup())) {
                return false;
            }
            if (!this.getName().equals(serviceInfo.getName())) {
                return false;
            }
            if (!this.getHostAddress().equals(serviceInfo.getHostAddress())) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String name = "group:" + this.group + ",name:" + this.name;
        if (hostAddress != null) {
            name += ",hostAddress:(" + hostAddress.toString() + ")";
        }
        return name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}

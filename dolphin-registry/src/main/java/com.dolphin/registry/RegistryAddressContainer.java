package com.dolphin.registry;

import java.util.List;

import com.dolphin.core.protocle.HostAddress;

/**
 * 地址注册容器
 * @author tianxiao
 * @version $Id: RegistryAddressContainer.java, v 0.1 2016年7月19日 上午11:13:45 tianxiao Exp $
 */
public interface RegistryAddressContainer extends HostAddressGetter {

    /**
     * 获取所有已注册的服务地址
     * @author tianxiao
     * @return
     * @version 2016年7月19日 上午11:13:59 tianxiao
     */
    List<HostAddress> getAll();

    /**
     * 添加服务
     * @author tianxiao
     * @param address
     * @version 2016年7月19日 上午11:14:21 tianxiao
     */
    void add(HostAddress address);

    /**
     * 移除服务
     * @author tianxiao
     * @param address
     * @version 2016年7月19日 上午11:14:33 tianxiao
     */
    void remove(HostAddress address);

}

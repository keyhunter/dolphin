package com.dolphin.registry;

import com.dolphin.core.protocle.HostAddress;

import java.util.List;

public interface HostAddressGetter {
    /**
     * 获取所有已注册的服务地址
     *
     * @return
     * @author tianxiao
     * @version 2016年7月19日 上午11:13:59 tianxiao
     */
    List<HostAddress> getAll();
}

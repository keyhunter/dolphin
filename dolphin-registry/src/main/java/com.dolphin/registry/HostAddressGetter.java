package com.dolphin.registry;

import java.util.List;

import com.dolphin.core.protocle.HostAddress;

public interface HostAddressGetter {
    /**
     * 获取所有已注册的服务地址
     * @author tianxiao
     * @return
     * @version 2016年7月19日 上午11:13:59 tianxiao
     */
    List<HostAddress> getAll();
}

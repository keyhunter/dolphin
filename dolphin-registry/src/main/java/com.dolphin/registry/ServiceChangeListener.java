package com.dolphin.registry;

/**
 * 服务发生变化的监听器
 * @author tianxiao
 * @version $Id: ServiceChangeListener.java, v 0.1 2016年7月19日 上午11:14:48 tianxiao Exp $
 */
public interface ServiceChangeListener {

    void change(String group, String serviceName);


}

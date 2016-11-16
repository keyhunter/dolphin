package com.dolphin.core.config;

import java.net.URL;

/**
 * 程序配置
 * @author jiujie
 * @version $Id: Config.java, v 0.1 2016年5月9日 下午4:54:16 jiujie Exp $
 */
public interface Config {

    /**
     * 获取配置的地址
     * @author jiujie
     * 2016年5月9日 下午4:26:52
     * @return
     */
    String getPath();

    /**
     * 获取资源配置
     * @author jiujie
     * 2016年5月9日 下午4:32:46
     * @return
     */
    URL getResource();

}

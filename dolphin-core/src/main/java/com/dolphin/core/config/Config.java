package com.dolphin.core.config;

import java.net.URL;

/**
 * 程序配置
 *
 * @author keyhunter
 * @version $Id: Config.java, v 0.1 2016年5月9日 下午4:54:16 keyhunter Exp $
 */
public interface Config {

    /**
     * 获取配置的地址
     *
     * @return
     * @author keyhunter
     * 2016年5月9日 下午4:26:52
     */
    String getPath();

    /**
     * 获取资源配置
     *
     * @return
     * @author keyhunter
     * 2016年5月9日 下午4:32:46
     */
    URL getResource();

}

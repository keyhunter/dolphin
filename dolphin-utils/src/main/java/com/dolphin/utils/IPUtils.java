package com.dolphin.utils;

import java.net.*;
import java.util.Collections;
import java.util.Enumeration;

/**
 * 获取当前机器第一个网卡的ip
 *
 * @author keyhunter
 *         Created on 2017/8/4.
 */
public class IPUtils {

    /**
     * 获取本机ip
     *
     * @return
     */
    public static String getCurrentIp() {
        StringBuilder sb = new StringBuilder();
        try {
            // 获取第一个非回环 ipv4 地址
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            label:
            for (NetworkInterface netint : Collections.list(nets)) {
                for (InetAddress addr : Collections.list(netint.getInetAddresses())) {
                    if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                        sb.append(addr.getHostAddress());
                        break label;
                    }
                }
            }
            // 没有ipv4地址
            if (sb.length() == 0)
                sb.append(InetAddress.getLocalHost().getHostAddress());
        } catch (SocketException e) {
        } catch (UnknownHostException e) {
        }

        return sb.toString();
    }

}

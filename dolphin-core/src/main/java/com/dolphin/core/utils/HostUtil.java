package com.dolphin.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class HostUtil {

    private static final Logger logger = LoggerFactory.getLogger(HostUtil.class);

    /**
     * 获取本机IP
     *
     * @return
     * @author keyhunter
     * 2016年7月12日 上午11:23:46
     */
    @Deprecated
    public static String getIp() {
        InetAddress ia = null;
        try {
            ia = ia.getLocalHost();
        } catch (Exception e) {
            logger.error("getIp error", e);
        }
        if (ia == null) {
            return null;
        }
        return ia.getHostAddress();
    }

    /**
     * 通过网卡，IP网段的正则匹配来查找本机IP
     *
     * @param ipRegex IP网段的正则表达式
     * @return
     * @author keyhunter
     * 2016年7月28日 下午12:41:00
     */
    public static String getIp(String ipRegex) {
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface nextElement = networkInterfaces.nextElement();
                if (nextElement.isUp()) {
                    Enumeration<InetAddress> inetAddresses = nextElement.getInetAddresses();
                    if (inetAddresses == null) {
                        continue;
                    }
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if (inetAddress instanceof Inet4Address) {
                            String hostAddress = inetAddress.getHostAddress();
                            if (hostAddress.equals("127.0.0.1")) {
                                continue;
                            }
                            if (hostAddress.matches(ipRegex)) {
                                return hostAddress;
                            }
                        }
                    }
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        return null;

    }

    /**
     * 获取端口是否被占用
     *
     * @param port
     * @return
     * @author keyhunter
     * 2016年7月12日 上午11:23:54
     */
    public static boolean isPortBound(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            return true;
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        logger.info("172.16.1.2".matches("172\\.16\\.1\\..*") + "");
        logger.info("172.160.1.2".matches("172\\.16\\.1\\..*") + "");
        logger.info("172.160.11.2".matches("172\\.16\\.1\\..*") + "");
    }
}

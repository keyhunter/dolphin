package com.dolphin.rpc.core.utils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;

public class HostUtil {

    /**
     * 获取本机IP
     * @author jiujie
     * 2016年7月12日 上午11:23:46
     * @return
     */
    @Deprecated
    public static String getIp() {
        InetAddress ia = null;
        try {
            ia = ia.getLocalHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ia == null) {
            return null;
        }
        return ia.getHostAddress();
    }

    /**
     * 通过网卡，IP网段的正则匹配来查找本机IP
     * @author jiujie
     * 2016年7月28日 下午12:41:00
     * @param ipRegex IP网段的正则表达式
     * @return
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
     * @author jiujie
     * 2016年7月12日 上午11:23:54
     * @param port
     * @return
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
        System.out.println("172.16.1.2".matches("172\\.16\\.1\\..*"));
        System.out.println("172.160.1.2".matches("172\\.16\\.1\\..*"));
        System.out.println("172.160.11.2".matches("172\\.16\\.1\\..*"));
    }
}

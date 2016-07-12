package com.dolphin.rpc.core.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class HostUtil {

    /**
     * 获取本机IP
     * @author jiujie
     * 2016年7月12日 上午11:23:46
     * @return
     */
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
        System.out.println(isPortBound(100));
    }
}

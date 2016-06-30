package com.dolphin.rpc.core.utils;

import java.net.InetAddress;

public class HostUtil {

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

    public static void main(String[] args) {
        System.out.println(getIp());
    }
}

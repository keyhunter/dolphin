package com.dolphin.utils;

import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求id生成器
 *
 * @author keyhunter
 *         Created on 2017/8/4.
 */
public class RequestIdGenerator {

    private static final String HOST_IP = IPUtils.getCurrentIp();

    private static final String PID = getPid();
    private static final String SPLITOR = "-";

    /**
     * 计数器，到最大值置1
     */
    private final AtomicInteger counter = new AtomicInteger();

    public String generate() {
        return HOST_IP + SPLITOR +
                PID + SPLITOR +
                System.currentTimeMillis() + SPLITOR +
                getLastIdSuffix();
    }

    /**
     * 获取id最后一位，最后一位为正数，到最大值循环开始
     *
     * @return
     */
    private int getLastIdSuffix() {
        int count = counter.get();
        int target = count >= Integer.MAX_VALUE ? 1 : count + 1;
        while (!counter.compareAndSet(count, target)) {
            count = counter.get();
            target = count >= Integer.MAX_VALUE ? 1 : count + 1;
        }
        return target;
    }

    public static void main(String[] args) {
        RequestIdGenerator idGenerator = new RequestIdGenerator();
        for (int i = 0; i < 100; i++) {
            String generate = idGenerator.generate();
            System.out.println(generate);
        }
    }

    /**
     * 获取进程id
     *
     * @return
     */
    private static String getPid() {
        // get name representing the running Java virtual machine.
        String name = ManagementFactory.getRuntimeMXBean().getName();
        // get pid
        String pid = name.split("@")[0];
        return pid;
    }

}

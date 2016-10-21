package com.dolphin.rpc.core.io.heart;

public interface HeartBeatable {

    boolean beat();

    long getBeatInterval();

    boolean isHealth();
}

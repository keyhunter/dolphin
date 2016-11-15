package com.dolphin.core.protocle.heart;

public interface HeartBeatable {

    boolean beat();

    long getBeatInterval();

    boolean isHealth();
}

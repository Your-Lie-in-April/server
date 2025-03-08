package com.appcenter.timepiece.global.util;


public enum LinkValidTime {
    DAY(1),
    WEEK(7),
    MONTH(31);

    private final int value;

    public int value() {
        return value;
    }

    LinkValidTime(int value) {
        this.value = value;
    }
}

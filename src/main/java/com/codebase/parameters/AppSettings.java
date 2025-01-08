package com.codebase.parameters;

public enum AppSettings {

    EXECUTOR_THREAD_COUNT(4);

    public final int value;

    AppSettings(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

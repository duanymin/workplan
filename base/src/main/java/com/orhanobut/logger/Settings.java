package com.orhanobut.logger;

import org.jetbrains.annotations.NotNull;

/**
 * @author Orhan Obut
 */
public final class Settings {

    private int methodCount = 2;
    private boolean showThreadInfo = true;

    /**
     * Determines how logs will printed
     */
    private LogLevel logLevel = LogLevel.FULL;

    @NotNull
    public Settings hideThreadInfo() {
        showThreadInfo = false;
        return this;
    }

    @NotNull
    public Settings setMethodCount(int methodCount) {
        this.methodCount = methodCount;
        return this;
    }

    @NotNull
    public Settings setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public int getMethodCount() {
        return methodCount;
    }

    public boolean isShowThreadInfo() {
        return showThreadInfo;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }
}

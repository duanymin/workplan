package com.orhanobut.logger;

import org.jetbrains.annotations.NotNull;

/**
 * @author Orhan Obut
 */
public interface Printer {

    @NotNull
    Printer t(String tag, int methodCount);

    @NotNull
    Settings init(String tag);

    void d(String message, Object... args);

    void e(String message, Object... args);

    void e(Throwable throwable, String message, Object... args);

    void w(String message, Object... args);

    void i(String message, Object... args);

    void v(String message, Object... args);

    void wtf(String message, Object... args);

    void json(String json);

    void xml(String xml);
}

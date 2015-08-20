package com.anno;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
// 这个标注应用于局部变量
@Retention(RetentionPolicy.RUNTIME)
// 标注会一直保留到运行时
@Documented
// 将此注解包含在javadoc中
public @interface ActionBarSet {
    public static enum WAY {
        TITLE, METHOD
    }

    @NotNull WAY way() default WAY.TITLE;

    @NotNull String title() default "";

    boolean homeAsUpEnabled() default false;
}
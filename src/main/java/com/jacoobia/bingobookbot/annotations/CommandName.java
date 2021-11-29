package com.jacoobia.bingobookbot.annotations;


import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandName {
    String value() default "";
}

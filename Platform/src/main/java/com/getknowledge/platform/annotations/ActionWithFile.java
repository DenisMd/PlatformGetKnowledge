package com.getknowledge.platform.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
public @interface ActionWithFile {
    String name() default "";
    String[] mandatoryFields() default "";
    int maxSize() default 4096; //4 Mb
}

package com.github.projectrake.sagittarius.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 23.11.2017.
 * <p>
 * Indicates that a class has been patched and modified by sagittarius.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Patched {
}

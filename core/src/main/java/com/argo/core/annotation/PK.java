package com.argo.core.annotation;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: Yaming
 * Date: 2014/10/5
 * Time: 18:23
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PK {

    String value() default "";

}

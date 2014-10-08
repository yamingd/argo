package com.argo.core.annotation;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2014/10/8.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Model {

    Class<?> value() default Object.class;

}

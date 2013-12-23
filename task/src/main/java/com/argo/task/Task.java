package com.argo.task;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 
 *
 * @author yaming_deng
 * @date 2013-1-9
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Component
public @interface Task {

	String value();
}

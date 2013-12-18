package com.argo.core.service.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 声明类为远程服务，在某种情况下也可以作为本地服务.
 *
 * @author yaming_deng
 * @date 2013-1-9
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Component
public @interface RmiService {
	
	String value() default "";
	
	/**
	 * Set the name of the exported RMI service, i.e. rmi://host:port/NAME
	 * @return
	 */
	String servcieName() default "";
	
	/**
	 * Set the interface of the service to export.
	 * @return
	 */
	Class<?> serviceInterface();
	
	/**
	 * Set the port of the registry for the exported RMI service. i.e. rmi://host:PORT/name
	 * @return
	 */
	String port() default "10990";
	
	/**
	 * @return
	 */
	String servicePort() default "10991";
	
	/**
	 * @return
	 */
	boolean alwaysCreateRegistry() default false;
	
	/**
	 * @return
	 */
	boolean replaceExistingBinding() default true;
}

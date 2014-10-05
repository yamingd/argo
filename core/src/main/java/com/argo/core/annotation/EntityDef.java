package com.argo.core.annotation;

import java.lang.annotation.*;

/**
 * 实体定义的注解
 *  
 *  @EntityDef(table="t_person",id=1)
 *  public class Person{
 *  	//...
 *  }
 *  
 * @author yaming_deng
 * @date 2013-1-17
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EntityDef {
	
	String value() default "";
	/**
	 * 对应的表名
	 * @return
	 */
	String table() default "";
	/**
	 * 唯一标识
	 * @return
	 */
	int id() default -1;
}

package com.argo.core.annotation;

import java.lang.annotation.*;

/**
 * 实体定义的注解
 *  
 *  @EntityDef(table="t_person",defid=1)
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
	int defid() default -1;
	
	/**
	 * 主键字段名，多个用逗号分隔
	 * @return
	 */
	String pkColumns() default "id";
	
	/**
	 * 主键属性名，多个用逗号分隔
	 * @return
	 */
	String pkFields() default "id";
}

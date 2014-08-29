package com.argo.service.factory;


import org.apache.commons.lang3.StringUtils;

/**
 * 描述 ：
 *
 * @author yaming_deng
 * @date 2013-1-11
 */
public class ServiceNameBuilder {
	
	/**
	 *  将com.service.HelloService 变成 helloService
	 * @param clazz
	 * @return
	 */
	public static String get(Class<?> clazz, String name){
		if(StringUtils.isNotBlank(name)){
			return name;
		}
		String[] temp = clazz.getName().split("\\.");
		String beanName = temp[temp.length-1];
		beanName = beanName.substring(0, 1).toLowerCase()+beanName.substring(1);
		return beanName;
	}
}

package com.argo.core.configuration;

import java.util.Map;

/**
 * 应用配置变化响应监听器.
 * @author yaming_deng
 *
 */
public interface ConfigListener {
	
	String getConfName();
	/**
	 * 触发配置的初始化.
	 * @param name
	 * @param map
	 */
	void onInit(String name, Map<String, Object> map);
	/**
	 * 某个配置Key的值发生变化.
	 * @param name
	 * @param key
	 * @param value
	 */
	void onChanged(String name, String key, Object value);
}

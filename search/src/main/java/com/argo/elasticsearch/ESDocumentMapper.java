package com.argo.elasticsearch;

public interface ESDocumentMapper {
	
	/**
	 * 转为JSON.
	 * @param object
	 * @return
	 */
	<T> String toJSON(T object);
	<T> String toJSON(T object, boolean ignoreNull);
	/**
	 * 从JSON转为实体.
	 * @param json
	 * @return
	 */
	<T> T asObject(Class<T> clazz, String json);
}

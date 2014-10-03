package com.argo.elasticsearch;

import com.argo.core.json.JsonUtil;

public class DefaultDocMapper implements ESDocumentMapper {

	@Override
	public <T> String toJSON(T object) {
        String s = JsonUtil.toJson(object);
        return s;
	}

	@Override
	public <T> String toJSON(T object, boolean ignoreNull) {
		String s = JsonUtil.toJson(object);
        return s;
	}

	@Override
	public <T> T asObject(Class<T> clazz, String json) {
		T o = JsonUtil.asT(clazz, json);
        return o;
	}

}

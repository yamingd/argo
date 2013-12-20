package com.argo.elasticsearch;

import com.argo.core.json.GsonUtil;

public class DefaultDocMapper implements ESDocumentMapper {

	@Override
	public <T> String toJSON(T object) {
        String s = GsonUtil.toJson(object);
        return s;
	}

	@Override
	public <T> String toJSON(T object, boolean ignoreNull) {
		String s = GsonUtil.toJson(object);
        return s;
	}

	@Override
	public <T> T asObject(Class<T> clazz, String json) {
		T o = GsonUtil.asT(clazz, json);
        return o;
	}

}

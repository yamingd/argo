package com.argo.elasticsearch;

import com.argo.core.json.GsonUtil;

public class DefaultDocMapper implements ESDocumentMapper {

	@Override
	public <T> String toJSON(T object) {
        return GsonUtil.toJson(object);
	}

	@Override
	public <T> String toJSON(T object, boolean ignoreNull) {
		return GsonUtil.toJson(object);
	}

	@Override
	public <T> T asObject(Class<T> clazz, String json) {
		return GsonUtil.asT(clazz, json);
	}

}

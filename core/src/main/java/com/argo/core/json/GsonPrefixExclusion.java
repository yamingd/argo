package com.argo.core.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * gson转换器的过滤器：转换的时候不转换被指定的属性名称
 *
 */
public class GsonPrefixExclusion implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        String fieldName = f.getName();
        if (fieldName.startsWith("_")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

}

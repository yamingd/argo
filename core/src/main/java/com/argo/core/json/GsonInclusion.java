package com.argo.core.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * gson转换的过滤器：只转换被设置的属性列表
 *
 */
public class GsonInclusion implements ExclusionStrategy {
    private List<String> inclusionFields;//要被转换的属性名称集合

    /**
     * 添加只需要被转换的属性名称
     *
     * @param fieldName 属性名称的可变数组
     * @return
     */
    public GsonInclusion addInclusionFields(String... fieldName) {
        if (inclusionFields == null) {
            inclusionFields = new ArrayList<String>();
        }
        if (fieldName != null && fieldName.length > 0) {
            for (int i = 0; i < fieldName.length; i++) {
                inclusionFields.add(fieldName[i]);
            }
        }
        return this;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        String fieldName = f.getName();
        if (inclusionFields.contains(fieldName)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

    public List<String> getInclusionFields() {
        return inclusionFields;
    }

    public void setInclusionFields(List<String> inclusionFields) {
        this.inclusionFields = inclusionFields;
    }

}

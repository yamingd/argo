package com.argo.core.web;

import com.argo.core.json.JsonUtil;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by yaming_deng on 14-8-20.
 */
public class JsonResponse<T> extends MvcResponse {

    protected List<T> data;

    public JsonResponse() {
        super();
        this.data = Lists.newArrayList();
    }

    public void add(T o){
       this.data.add(o);
    }

    public List<T> getData() {
        return data;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}

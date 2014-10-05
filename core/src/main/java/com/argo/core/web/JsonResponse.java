package com.argo.core.web;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by yaming_deng on 14-8-20.
 */
public class JsonResponse extends MvcResponse {

    protected List<Object> data;

    public JsonResponse() {
        super();
        this.data = Lists.newArrayList();
    }

    public void add(Object o){
       this.data.add(o);
    }

    public List<Object> getData() {
        return data;
    }
}

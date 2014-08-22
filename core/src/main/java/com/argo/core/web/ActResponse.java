package com.argo.core.web;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yaming_deng on 14-8-20.
 */
public class ActResponse implements Serializable {

    private String msg;
    private Integer code;
    private List<Object> data;

    public ActResponse() {
        this.code = 200;
        this.msg = "";
        this.data = Lists.newArrayList();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }
}

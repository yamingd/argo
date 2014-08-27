package com.argo.core.email;

import com.argo.core.base.BaseEntity;

/**
 * Created by yaming_deng on 14-8-27.
 */
public class EmailTemplate extends BaseEntity {

    private Integer id;
    private String title;
    private String ftlPath;
    private String name;
    private Integer enabled;

    @Override
    public String getPK() {
        return this.id + "";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFtlPath() {
        return ftlPath;
    }

    public void setFtlPath(String ftlPath) {
        this.ftlPath = ftlPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

}

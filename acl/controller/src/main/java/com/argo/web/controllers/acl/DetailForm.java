package com.argo.web.controllers.acl;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;

/**
 * Created by yaming_deng on 2014/10/8.
 */
@Scope("prototype")
public class DetailForm {

    @NotEmpty(message = "name_empty")
    @Length(min=0, max=20, message = "name_too_long")
    private String name;

    @NotEmpty(message = "title_empty")
    @Length(min=0, max=20, message = "title_too_long")
    private String title;

    private String url;
    private Integer kindId;

    private Integer parentId;
    private Integer orderNo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getKindId() {
        return kindId;
    }

    public void setKindId(Integer kindId) {
        this.kindId = kindId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }
}

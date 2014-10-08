package com.argo.web.controllers.acl;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by yaming_deng on 2014/10/8.
 */
public class DetailForm {

    @NotEmpty(message = "name")
    @Length(min=0, max=20)
    private String name;

    @NotEmpty(message = "title")
    @Length(min=0, max=20)
    private String title;

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
}

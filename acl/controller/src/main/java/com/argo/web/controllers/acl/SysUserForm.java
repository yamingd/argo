package com.argo.web.controllers.acl;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;

/**
 * Created by yaming_deng on 2014/10/8.
 */
@Scope("prototype")
public class SysUserForm {

    @NotEmpty(message = "name_empty")
    @Length(min=0, max=20, message = "name_too_long")
    private String name;

    @NotEmpty(message = "title_empty")
    @Length(min=0, max=20, message = "title_too_long")
    private String title;

    @Length(min=0, max=20, message = "passwd_too_long")
    private String passwd;

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

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

}

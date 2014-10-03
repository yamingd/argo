package com.company.web.controllers;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by yaming_deng on 14-8-21.
 */
public class DemoForm {

    @NotEmpty(message = "uname")
    private String uname;

    @NotEmpty(message = "email")
    @Email
    private String email;

    @NotEmpty(message = "passwd")
    private String passwd;

    @NotEmpty(message = "iconUrl")
    private String iconUrl;

    private String token;

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "DemoForm{" +
                "uname='" + uname + '\'' +
                ", email='" + email + '\'' +
                ", passwd='" + passwd + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}

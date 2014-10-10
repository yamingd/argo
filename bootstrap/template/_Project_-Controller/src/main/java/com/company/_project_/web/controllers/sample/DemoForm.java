package com.company._project_.web.controllers.sample;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

/**
 * Created by yaming_deng on 14-8-21.
 */
public class DemoForm {

    @NotEmpty(message = "uname")
    @Length(min=0, max=10)
    private String uname;

    @NotEmpty(message = "email")
    @Email
    private String email;

    @NotEmpty(message = "passwd")
    @Length(min=0, max=10)
    private String passwd;

    @NotEmpty(message = "iconUrl")
    private String iconUrl;

    @NotEmpty
    private Integer prize;

    @URL
    private String httpUrl;

    private String token;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @NotNull
    @Past
    private Date birthday;

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

    public Integer getPrize() {
        return prize;
    }

    public void setPrize(Integer prize) {
        this.prize = prize;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
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

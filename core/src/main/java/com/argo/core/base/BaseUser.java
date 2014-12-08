package com.argo.core.base;

import com.argo.core.annotation.Column;
import com.argo.core.annotation.PK;
import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * 描述 ：用户实体基类
 *
 * @author yaming_deng
 * @date 2012-12-7
 */
public class BaseUser extends BaseEntity {
    /**
     * 用户id
     *
     */
    @PK("uid")
    @Column
    private Long uid;

    /**
     * 登录id
     *
     */
    private String loginId;

    /**
     * 登录密码
     *
     */
    @Expose(serialize = false)
    private String passwd;

    /**
     * 最后登录时间
     *
     */
    private Date loginAt;

    /**
     * 更新时间
     *
     */
    private Date updateAt;

    /**
     * 是否确认(0/1)
     *
     */
    private Integer confirmed;

    /**
     * 密码方式
     */
    @Expose(serialize = false)
    private Integer passwdMode;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Date getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(Date loginAt) {
        this.loginAt = loginAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Integer getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Integer confirmed) {
        this.confirmed = confirmed;
    }

    public void setPasswdMode(Integer passwdMode) {
		this.passwdMode = passwdMode;
	}

	public Integer getPasswdMode() {
		return passwdMode;
	}

    public boolean isAnonymous(){
        return this.uid.intValue() <= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseUser)) return false;

        BaseUser baseUser = (BaseUser) o;

        if (!uid.equals(baseUser.uid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }

    @Override
    public String getPK() {
        return ":" + uid;
    }
}

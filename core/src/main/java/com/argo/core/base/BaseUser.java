package com.argo.core.base;

import java.util.Date;

/**
 * 描述 ：用户实体基类
 *
 * @author yaming_deng
 * @date 2012-12-7
 */
public class BaseUser extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -38551281936005697L;

	private Long uid;
	
	private String email;
	
	//登录名
	private String userName;
	
	//中文名/真实名称
	private String realName;
	
	private String loginIpAddress;
	
	private Date addAt;
	
	private Date updateAt;
	
	private String iconUrl;
	
	private String mobile;
	
	private String hashPasswd;
	private Integer passwdMode;
	
	@Override
	public String getPK() {
		return uid+"";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getLoginIpAddress() {
		return loginIpAddress;
	}

	public void setLoginIpAddress(String loginIpAddress) {
		this.loginIpAddress = loginIpAddress;
	}

	public Date getAddAt() {
		return addAt;
	}

	public void setAddAt(Date addAt) {
		this.addAt = addAt;
	}

	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Long getUid() {
		return uid;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobile() {
		return mobile;
	}

	public void setHashPasswd(String hashPasswd) {
		this.hashPasswd = hashPasswd;
	}

	public String getHashPasswd() {
		return hashPasswd;
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

}

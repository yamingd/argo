package com.argo.core.password;


import com.argo.core.base.BaseUser;

/**
 * 用来兼容各个集成系统的加密方式.
 * @author yaming_deng
 *
 */
public interface PasswordService {

	Integer getModeId();
	/**
	 * 生成加密密码，存放到数据库.
	 * @param password
	 * @param email
	 * @return
	 */
	String encrypt(String password, String email);
	
	/**
	 * 登录时，检验密码
	 * @param password
	 * @param email
	 * @param user
	 * @return
	 */
	boolean validate(String password, String email, BaseUser user);
}

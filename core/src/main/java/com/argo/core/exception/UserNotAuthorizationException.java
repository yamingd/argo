package com.argo.core.exception;


/**
 * 用户没登录异常
 *
 * @author yaming_deng
 * @date 2013-1-14
 */
public class UserNotAuthorizationException extends ServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 193969494463770881L;

	public UserNotAuthorizationException(String message, Throwable cause) {
		super(message, cause);
        this.setErrcode(60401);
	}

	public UserNotAuthorizationException(String message) {
		super(message);
        this.setErrcode(60401);
	}

}

package com.argo.core.exception;


public class UserAccountLockedException extends UserNotAuthorizationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 193969494463770881L;

	public UserAccountLockedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserAccountLockedException(String message) {
		super(message);
	}

}

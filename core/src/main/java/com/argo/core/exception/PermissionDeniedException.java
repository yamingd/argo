package com.argo.core.exception;


import com.argo.core.base.BaseException;

/**
 * 访问被拒绝
 *
 * @author yaming_deng
 * @date 2012-6-12
 */
public class PermissionDeniedException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5523672879674628318L;

	/**
	 * @param message
	 * @param params
	 */
	public PermissionDeniedException(String message, Object... params) {
		super(message, params);
	}

	/**
	 * @param message
	 * @param cause
	 * @param params
	 */
	public PermissionDeniedException(String message, Throwable cause,
			Object... params) {
		super(message, cause, params);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PermissionDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public PermissionDeniedException(String message) {
		super(message);
	}

}

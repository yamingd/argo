package com.argo.core.exception;


import com.argo.core.base.BaseException;

/**
 * 描述 ：
 *
 * @author yaming_deng
 * @date 2012-3-6
 */
public class ParamInvalidException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8751443848602435652L;

	/**
	 * @param message
	 * @param params
	 */
	public ParamInvalidException(String message, Object... params) {
		super(message, params);
	}

	/**
	 * @param message
	 * @param cause
	 * @param params
	 */
	public ParamInvalidException(String message, Throwable cause,
                                 Object... params) {
		super(message, cause, params);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ParamInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ParamInvalidException(String message) {
		super(message);
	}

}

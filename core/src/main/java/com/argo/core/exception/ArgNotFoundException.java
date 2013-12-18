package com.argo.core.exception;

/**
 * 缺少参数异常.
 * @author yaming_deng
 *
 */
public class ArgNotFoundException extends ServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7124692546160606497L;

	public ArgNotFoundException(String message, Object... paramters) {
		super(message, paramters);
	}

	public ArgNotFoundException(String message, Throwable cause,
			Object... paramters) {
		super(message, cause, paramters);
	}

	public ArgNotFoundException(String message) {
		super(message);
	}

}

package com.argo.core.exception;


import com.argo.core.base.BaseException;

public class ServiceException extends BaseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7939644166745218634L;
	
	public ServiceException(String message) {
		super(message);
	}
	
	public ServiceException(String message, Object... paramters) {
		super(message, paramters);
	}
	
	public ServiceException(String message, Throwable cause, Object... paramters) {
		super(message, cause, paramters);
	}
}

package com.argo.core.base;


import org.apache.commons.lang3.StringUtils;

public class BaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6628333484201118676L;

	public BaseException(String message, Throwable cause, Object... params){
		super(cause.getMessage()+":"+message+"@"+ StringUtils.join(params), cause);
	}
	
	public BaseException(String message, Object... params){
		super(message+"@"+StringUtils.join(params));
	}
	
	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}	
	
	public BaseException(String message) {
		super(message);
	}
}

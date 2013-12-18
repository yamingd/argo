package com.argo.core.exception;

/**
 * 描述 ：
 *
 * @author yaming_deng
 * @date 2012-2-3
 */
public class JsonDSException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6035264398370270633L;

	/**
	 * @param message
	 * @param cause
	 */
	public JsonDSException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public JsonDSException(String message) {
		super(message);
	}

}

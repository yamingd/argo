package com.argo.core.exception;


import com.argo.core.base.BaseException;

/**
 * @author yaming_deng
 *
 */
public class EntityNotFoundException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6977202602292745570L;

	public EntityNotFoundException(String tableName, String opCode,
			String message, Object... paramters) {
		super(message+":"+tableName+"("+opCode+")", paramters);
	}

	public EntityNotFoundException(String tableName, String opCode,
			String message, Throwable cause, Object... paramters) {
		super(message+":"+tableName+"("+opCode+")", cause, paramters);
	}
	
	
}

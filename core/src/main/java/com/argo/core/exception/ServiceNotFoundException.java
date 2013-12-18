package com.argo.core.exception;


import com.argo.core.base.BaseException;

/**
 * 描述 ：
 *
 * @author yaming_deng
 * @date 2012-12-17
 */
public class ServiceNotFoundException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1909899826434302659L;

	/**
	 * @param message
	 */
	public ServiceNotFoundException(String message) {
		super("ServiceName:=" + message);
	}

}

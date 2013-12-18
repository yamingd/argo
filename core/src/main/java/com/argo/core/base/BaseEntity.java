package com.argo.core.base;

import java.io.Serializable;


public abstract class BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6858560927202198091L;

	
	/**
	 * 返回主键.
	 * @return
	 */
	public abstract String getPK();
}

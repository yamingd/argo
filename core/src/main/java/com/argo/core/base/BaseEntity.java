package com.argo.core.base;

import java.io.Serializable;


public abstract class BaseEntity implements Serializable {
	/**
	 * 返回主键.
	 * @return
	 */
	public abstract String getPK();
}

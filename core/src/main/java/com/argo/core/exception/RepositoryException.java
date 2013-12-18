package com.argo.core.exception;


import com.argo.core.base.BaseException;

/**
 * 数据库层异常基类
 * @author yaming_deng
 *
 */
public class RepositoryException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6628333484201118676L;


	/**
	 * @param tableName: 数据表名称
	 * @param opCode：操作代号/英文名
	 * @param message：业务逻辑描述
	 * @param cause
	 * @param paramters: 执行参数
	 */
	public RepositoryException(String tableName, String opCode, String message, Throwable cause, Object... params) {
		super(message+":"+tableName+"("+opCode+")", cause, params);
	}

	/**
	 * @param tableName: 数据表名称
	 * @param opCode：操作代号/英文名
	 * @param message：业务逻辑描述
	 * @param paramters: 执行参数
	 */
	public RepositoryException(String tableName, String opCode, String message, Object... params) {
		super(message+":"+tableName+"("+opCode+")", params);
	}
	
}

package com.argo.db.hooks;

import com.jolbox.bonecp.ConnectionHandle;
import com.jolbox.bonecp.StatementHandle;
import com.jolbox.bonecp.hooks.AbstractConnectionHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Map;

/**
 * 
 * MySQL数据库SQL语句执行Hook
 *
 * @author yaming_deng
 * @date 2013-1-24
 */
public class MySQLConnectionHook extends AbstractConnectionHook {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void onBeforeStatementExecute(ConnectionHandle conn, StatementHandle statement, String sql, Map<Object, Object> params) {
		Connection internalConnection = conn.getInternalConnection();
		
		if(logger.isDebugEnabled()){
			logger.debug("onBeforeStatementExecute:{}", internalConnection);
		}

	}
	
	@Override
	public void onAfterStatementExecute(ConnectionHandle conn, StatementHandle statement, String sql, Map<Object, Object> params) {
		Connection internalConnection = conn.getInternalConnection();
	}
	
	public boolean onConnectionException(ConnectionHandle connection, String state, Throwable t) {
        logger.error("onConnectionException, {}, {}", connection, t);
		return true; // keep the default behaviour
	}

    @Override
    public void onCheckOut(ConnectionHandle connection) {
        super.onCheckOut(connection);
    }

    @Override
    public void onDestroy(ConnectionHandle connection) {
        logger.error("onDestroy, {}", connection);
    }
}

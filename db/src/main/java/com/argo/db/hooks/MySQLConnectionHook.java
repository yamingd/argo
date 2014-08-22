package com.argo.db.hooks;

import com.argo.db.context.DatafarmContext;
import com.argo.db.farm.ServerFarm;
import com.jolbox.bonecp.ConnectionHandle;
import com.jolbox.bonecp.StatementHandle;
import com.jolbox.bonecp.hooks.AbstractConnectionHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
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
			logger.debug("onBeforeStatementExecute:" + internalConnection);
			logger.debug("onBeforeStatementExecute:" + sql);
		}
		
		if(ServerFarm.current != null && ServerFarm.current.isShard()){
			//只有在启用Shard机制时，才需要
			String useSql = "USE " + DatafarmContext.getContext().getDbDef().getDbName();
			if(logger.isDebugEnabled()){
				logger.debug("onBeforeStatementExecute:" + useSql);
			}
			
			try {
				statement.getInternalStatement().execute(useSql);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	@Override
	public void onAfterStatementExecute(ConnectionHandle conn, StatementHandle statement, String sql, Map<Object, Object> params) {
		Connection internalConnection = conn.getInternalConnection();
		
		if(logger.isDebugEnabled()){
			logger.debug("onAfterStatementExecute:" + internalConnection);
			logger.debug("onAfterStatementExecute:" + sql);
		}
	}
	
	public boolean onConnectionException(ConnectionHandle connection, String state, Throwable t) {
		return true; // keep the default behaviour
	}
}

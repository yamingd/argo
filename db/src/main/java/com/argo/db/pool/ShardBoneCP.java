package com.argo.db.pool;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Shard BoneCP
 *
 * @author yaming_deng
 * @date 2013-1-24
 */
public class ShardBoneCP extends BoneCP {

	/**
	 * 
	 */
	private static final long serialVersionUID = -775320566920168983L;
	
	/** Logger class. */
	private static Logger logger = LoggerFactory.getLogger(ShardBoneCP.class);
	
	/**
	 * @param config
	 * @throws SQLException
	 */
	public ShardBoneCP(BoneCPConfig config) throws SQLException {
		super(config);
	}
	
	/** Returns a database connection by using Driver.getConnection() or DataSource.getConnection()
	 * @return Connection handle
	 * @throws SQLException on error
	 */
	protected Connection obtainRawInternalConnection()
	throws SQLException {
		Connection result = super.obtainRawInternalConnection();
		if(logger.isDebugEnabled()){
			logger.debug("obtainRawInternalConnection: [" + result + "]");
		}
		return result;
	}
}

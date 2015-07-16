package com.argo.db.pool;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Master-Slave BoneCP
 *
 * @author yaming_deng
 * @date 2013-1-24
 */
public class MSBoneCP extends BoneCP {

	/**
	 *
	 */
	private static final long serialVersionUID = -775320566920168983L;

	/** Logger class. */
	private static Logger logger = LoggerFactory.getLogger(MSBoneCP.class);

	/**
	 * @param config
	 * @throws java.sql.SQLException
	 */
	public MSBoneCP(BoneCPConfig config) throws SQLException {
		super(config);
	}

	/** Returns a database connection by using Driver.getConnection() or DataSource.getConnection()
	 * @return Connection handle
	 * @throws java.sql.SQLException on error
	 */
	protected Connection obtainRawInternalConnection()
	throws SQLException {
		Connection result = super.obtainRawInternalConnection();
//		if(logger.isDebugEnabled()){
//			logger.debug("obtainRawInternalConnection: [" + result + "]");
//		}
		return result;
	}
}

package com.argo.db.datasource;

import com.argo.db.pool.ShardBoneCP;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import com.jolbox.bonecp.PoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 分布式Shard数据源.
 * 
 * Client ---> Logical Nodes --> Physics Servers
 *
 * @author yaming_deng
 * @date 2013-1-24
 */
public class ShardPooledDataSource extends BoneCPDataSource{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8914094842865950071L;
	
	private static final Logger logger = LoggerFactory.getLogger(ShardPooledDataSource.class);

	/** Pool handle. */
	private transient volatile BoneCP pool = null;
	/** Lock for init. */
	private ReadWriteLock rwl = new ReentrantReadWriteLock();
	/**
	 * @param config
	 */
	public ShardPooledDataSource(BoneCPConfig config) {
		super(config);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.sql.DataSource#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		if (this.pool == null){
			initConnectionPoolM();
		}
		return this.pool.getConnection();
	}
	
	/**
	 * @throws SQLException 
	 * 
	 *
	 */
	protected void initConnectionPoolM() throws SQLException {
		this.rwl.readLock().lock();
		if (this.pool == null){
			this.rwl.readLock().unlock();
			this.rwl.writeLock().lock();
			if (this.pool == null){ //read might have passed, write might not
				try {
					if (this.getDriverClass() != null){
						loadClass(this.getDriverClass());
					}
				}
				catch (ClassNotFoundException e) {
					throw new SQLException(PoolUtil.stringifyException(e));
				}


				logger.debug(this.toString());

				this.pool = new ShardBoneCP(this);
			}

			this.rwl.writeLock().unlock(); // Unlock write
		} else {
			this.rwl.readLock().unlock(); // Unlock read
		}
	}
	
	/**
	 * Close the datasource. 
	 *
	 */
	public void close(){
		if (this.pool != null){
			this.pool.shutdown();
		}
	}
	
	/**
	 * Returns the total leased connections.
	 *
	 * @return total leased connections
	 */
	public int getTotalLeased() {
		return this.pool.getTotalLeased();
	}
}

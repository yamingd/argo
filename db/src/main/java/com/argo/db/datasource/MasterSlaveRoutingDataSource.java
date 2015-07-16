package com.argo.db.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 分布式数据源Routing
 *
 * @author yaming_deng
 * @date 2013-1-24
 */
public class MasterSlaveRoutingDataSource extends AbstractDataSource implements InitializingBean, DisposableBean {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private AtomicInteger shiftId = new AtomicInteger();

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		if(this.targetDataSources!=null){
            for (DataSource s : targetDataSources) {
                try {
                    ((MasterSlaveDataSource)s).close();
                } catch (Exception e) {

                }
            }
		}
	}
    private String name;
    private String role;

	private List<DataSource> targetDataSources;

	private DataSource defaultTargetDataSource;

	private boolean lenientFallback = true;

	private DataSourceLookup dataSourceLookup = new JndiDataSourceLookup();

	/**
	 * Specify the map of target DataSources, with the lookup key as key.
	 * The mapped value can either be a corresponding {@link javax.sql.DataSource}
	 * instance or a data source name String (to be resolved via a
	 * {@link #setDataSourceLookup DataSourceLookup}).
	 */
	public void setTargetDataSources(List<DataSource> targetDataSources) {
		this.targetDataSources = targetDataSources;
	}

	/**
	 * Specify the default target DataSource, if any.
	 * <p>The mapped value can either be a corresponding {@link javax.sql.DataSource}
	 * instance or a data source name String (to be resolved via a
	 * {@link #setDataSourceLookup DataSourceLookup}).
	 * <p>This DataSource will be used as target if none of the keyed
	 * {@link #setTargetDataSources targetDataSources} match the
	 */
	public void setDefaultTargetDataSource(DataSource defaultTargetDataSource) {
		this.defaultTargetDataSource = defaultTargetDataSource;
	}

	/**
	 * Specify whether to apply a lenient fallback to the default DataSource
	 * if no specific DataSource could be found for the current lookup key.
	 * <p>Default is "true", accepting lookup keys without a corresponding entry
	 * in the target DataSource map - simply falling back to the default DataSource
	 * in that case.
	 * <p>Switch this flag to "false" if you would prefer the fallback to only apply
	 * if the lookup key was <code>null</code>. Lookup keys without a DataSource
	 * entry will then lead to an IllegalStateException.
	 * @see #setTargetDataSources
	 * @see #setDefaultTargetDataSource
	 */
	public void setLenientFallback(boolean lenientFallback) {
		this.lenientFallback = lenientFallback;
	}

	/**
	 * Set the DataSourceLookup implementation to use for resolving data source
	 * name Strings in the {@link #setTargetDataSources targetDataSources} map.
	 * <p>Default is a {@link JndiDataSourceLookup}, allowing the JNDI names
	 * of application server DataSources to be specified directly.
	 */
	public void setDataSourceLookup(DataSourceLookup dataSourceLookup) {
        this.dataSourceLookup = (dataSourceLookup != null ? dataSourceLookup : new JndiDataSourceLookup());
	}


	public void afterPropertiesSet() {
		if (this.targetDataSources == null) {
			throw new IllegalArgumentException("Property 'targetDataSources' is required");
		}
	}

	public Connection getConnection() throws SQLException {
		Connection conn = determineTargetDataSource().getConnection();
//		if(logger.isDebugEnabled()){
//			this.logger.debug("getConnection: [" + conn + "]");
//		}
		return conn;
	}

	public Connection getConnection(String username, String password) throws SQLException {
		Connection conn = determineTargetDataSource().getConnection(username, password);
//		if(logger.isDebugEnabled()){
//			this.logger.debug("getConnection: [" + conn + "]");
//		}
		return conn;
	}

    protected DataSource determineTargetDataSource() {
        if (this.targetDataSources.size() == 1){
            return targetDataSources.get(0);
        }
        int count = shiftId.getAndIncrement();
        int index = count % targetDataSources.size();
		return targetDataSources.get(index);
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

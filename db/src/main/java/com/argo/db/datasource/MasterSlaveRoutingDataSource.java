package com.argo.db.datasource;

import com.argo.db.context.MasterSlaveContext;
import com.argo.db.farm.ShardDbDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

	/**
	 * Determine the current lookup key. This will typically be
	 * implemented to check a thread-bound transaction context.
	 * <p>Allows for arbitrary keys. The returned key needs
	 * to match the stored lookup key type, as resolved by the
	 * {@link #resolveSpecifiedLookupKey} method.
	 */
	protected Object determineCurrentLookupKey() {
        MasterSlaveContext context = MasterSlaveContext.getContext();
		if(context.getTargetName()==null){
			return null;
		}
		return context.getTargetName()+"-"+context.getTargetRole();
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		if(this.targetDataSources!=null){
			Iterator<Object> itor = this.targetDataSources.keySet().iterator();
			while(itor.hasNext()){
				Object key = itor.next();
                List<DataSource> source = this.targetDataSources.get(key);
                for (DataSource s : source) {
                    try {
                        ((MasterSlaveDataSource)s).close();
                    } catch (Exception e) {

                    }
                }
			}
		}
	}

	private Map<Object, List<DataSource>> targetDataSources;

	private Object defaultTargetDataSource;

	private boolean lenientFallback = true;

	private DataSourceLookup dataSourceLookup = new JndiDataSourceLookup();

	private Map<Object, DataSource> resolvedDataSources;

	private DataSource resolvedDefaultDataSource;

	/**
	 * Specify the map of target DataSources, with the lookup key as key.
	 * The mapped value can either be a corresponding {@link javax.sql.DataSource}
	 * instance or a data source name String (to be resolved via a
	 * {@link #setDataSourceLookup DataSourceLookup}).
	 * <p>The key can be of arbitrary type; this class implements the
	 * generic lookup process only. The concrete key representation will
	 * be handled by {@link #resolveSpecifiedLookupKey(Object)} and
	 * {@link #determineCurrentLookupKey()}.
	 */
	public void setTargetDataSources(Map<Object, List<DataSource>> targetDataSources) {
		this.targetDataSources = targetDataSources;
	}

	/**
	 * Specify the default target DataSource, if any.
	 * <p>The mapped value can either be a corresponding {@link javax.sql.DataSource}
	 * instance or a data source name String (to be resolved via a
	 * {@link #setDataSourceLookup DataSourceLookup}).
	 * <p>This DataSource will be used as target if none of the keyed
	 * {@link #setTargetDataSources targetDataSources} match the
	 * {@link #determineCurrentLookupKey()} current lookup key.
	 */
	public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
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
	 * @see #determineCurrentLookupKey()
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
		if(logger.isDebugEnabled()){
			this.logger.debug("getConnection: [" + conn + "]");
		}
		return conn;
	}

	public Connection getConnection(String username, String password) throws SQLException {
		Connection conn = determineTargetDataSource().getConnection(username, password);
		if(logger.isDebugEnabled()){
			this.logger.debug("getConnection: [" + conn + "]");
		}
		return conn;
	}

	/**
	 * Retrieve the current target DataSource. Determines the
	 * {@link #determineCurrentLookupKey() current lookup key}, performs
	 * a lookup in the {@link #setTargetDataSources targetDataSources} map,
	 * falls back to the specified
	 * {@link #setDefaultTargetDataSource default target DataSource} if necessary.
	 * @see #determineCurrentLookupKey()
	 */
	protected DataSource determineTargetDataSource() {
		Assert.notNull(this.resolvedDataSources, "DataSource router not initialized");
		Object lookupKey = determineCurrentLookupKey();
		List<DataSource> dataSource = this.targetDataSources.get(lookupKey);
		if (dataSource == null) {
			throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
		}
        if (dataSource.size() == 1){
            return dataSource.get(0);
        }
        int count = shiftId.getAndIncrement();
        int index = count % dataSource.size();
		return dataSource.get(index);
	}


	/**
	 * Resolve the given lookup key object, as specified in the
	 * {@link #setTargetDataSources targetDataSources} map, into
	 * the actual lookup key to be used for matching with the
	 * {@link #determineCurrentLookupKey() current lookup key}.
	 * <p>The default implementation simply returns the given key as-is.
	 * @param lookupKey the lookup key object as specified by the user
	 * @return the lookup key as needed for matching
	 */
	protected Object resolveSpecifiedLookupKey(Object lookupKey) {
		return lookupKey;
	}
}

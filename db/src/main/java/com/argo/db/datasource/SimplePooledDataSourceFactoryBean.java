package com.argo.db.datasource;

import com.argo.db.JdbcConfig;
import com.jolbox.bonecp.BoneCPConfig;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;

/**
 * 
 * 单机数据源工厂Bean.
 * Application.dev.yaml 配置如下
 * 
 * Oracle数据源
 * jdbc.engine : "oracle" #其他如：mysql,sqlserver,sqlserver2k
   jdbc.user   : "test"
   jdbc.passwd : "test"
   jdbc.url : "jdbc:oracle:thin:@192.168.39.67:1521:orcl" #
 
   jdbc.pool.idleConnectionTestPeriod : 60
   jdbc.pool.idleMaxAge : 240
   jdbc.pool.maxConnectionsPerPartition : 9
   jdbc.pool.minConnectionsPerPartition : 3
   jdbc.pool.partitionCount : 3
   jdbc.pool.acquireIncrement : 2
   jdbc.pool.statementsCacheSize : 100
   jdbc.pool.releaseHelperThreads : 3
   jdbc.pool.queryExecuteTimeLimitInMs : 3000

   或者
   
   jdbc.{name}.engine : "oracle"
   jdbc.{name}.user : "test"
   jdbc.{name}.passwd : "test"
   jdbc.{name}.url : "url"
   
 *
 * @author yaming_deng
 * @date 2013-1-24
 */
public class SimplePooledDataSourceFactoryBean implements FactoryBean<ShardPooledDataSource>, InitializingBean, DisposableBean  {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private JdbcConfig jdbcConfig;
	
	private ShardPooledDataSource shardPooledDataSource;
	
	private String name = "";
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public ShardPooledDataSource getObject() throws Exception {
		return shardPooledDataSource;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return ShardPooledDataSource.class;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

        Map cfg = this.jdbcConfig.getServer(this.name);

		BoneCPConfig config = new BoneCPConfig();
		config.setDisableJMX(false);
		config.setUsername(ObjectUtils.toString(cfg.get("user")));
		config.setPassword(ObjectUtils.toString(cfg.get("pwd")));
		config.setProperties(this.jdbcConfig.getPoolProperties());
		config.setStatisticsEnabled(true);
		config.setJdbcUrl(ObjectUtils.toString(cfg.get("url")));
		
		this.shardPooledDataSource = new ShardPooledDataSource(config);
		
	}
		
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		this.shardPooledDataSource.close();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

    public JdbcConfig getJdbcConfig() {
        return jdbcConfig;
    }

    public void setJdbcConfig(JdbcConfig jdbcConfig) {
        this.jdbcConfig = jdbcConfig;
    }
}

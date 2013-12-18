package com.argo.db.datasource;

import com.argo.db.DbEngineEnum;
import com.argo.db.JdbcConfig;
import com.argo.db.hooks.MySQLConnectionHook;
import com.argo.db.hooks.OracleConnectionHook;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.hooks.ConnectionHook;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 分布式数据源工厂Bean.
 * 
 * Factory --> ShardRoutingDataSource --> ShardPooledDataSource
 *
 * @author yaming_deng
 * @date 2013-1-24
 */
public class ShardPooledDataSourceFactoryBean implements FactoryBean<ShardRoutingDataSource>, InitializingBean, DisposableBean  {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private ShardRoutingDataSource shardRoutingDataSource;

    private JdbcConfig jdbcConfig;
    private List servers;
    private String engineType;

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public ShardRoutingDataSource getObject() throws Exception {
		return shardRoutingDataSource;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return ShardRoutingDataSource.class;
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
		this.shardRoutingDataSource = new ShardRoutingDataSource();
        this.servers = this.jdbcConfig.getFarms();
        if (this.servers == null || this.servers.size() == 0){
            throw new Exception("jdbc.yaml dones't find any farm configuration. please check.");
        }
        Map temp = this.jdbcConfig.getCommon();
        if(temp == null || temp.size() == 0){
            throw new Exception("jdbc.yaml dones't find any common configuration. please check.");
        }
        this.engineType = ObjectUtils.toString(temp.get("type"));

		BoneCPConfig config = new BoneCPConfig();
		config.setDisableJMX(false);
		config.setUsername(ObjectUtils.toString(temp.get("user")));
		config.setPassword(ObjectUtils.toString(temp.get("pwd")));
		config.setProperties(this.jdbcConfig.getPoolProperties());
		config.setStatisticsEnabled(true);
		config.setConnectionHook(getConnectionHook());

		//业务Shard数据源
		Map<Object, Object> dsMap = new HashMap<Object, Object>();
		for (int i=0; i<this.servers.size(); i++) {
            Map server = (Map)this.servers.get(i);
			String jdbcUrl = ObjectUtils.toString(server.get("server"));
			config.setJdbcUrl(jdbcUrl);
			ShardPooledDataSource source = new ShardPooledDataSource(config);
			dsMap.put(server, source);
			this.logger.info("@@@Init ShardPooledDataSource: {}", config.getJdbcUrl());
		}
		this.shardRoutingDataSource.setTargetDataSources(dsMap);
		//初始化
		this.shardRoutingDataSource.afterPropertiesSet();
	}
	
	private ConnectionHook getConnectionHook(){
		if(this.engineType.equalsIgnoreCase(DbEngineEnum.ORACLE)){
			return new OracleConnectionHook();
		}else if(this.engineType.equalsIgnoreCase(DbEngineEnum.MYSQL)){
			return new MySQLConnectionHook();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		this.shardRoutingDataSource.destroy();		
	}

    public JdbcConfig getJdbcConfig() {
        return jdbcConfig;
    }

    public void setJdbcConfig(JdbcConfig jdbcConfig) {
        this.jdbcConfig = jdbcConfig;
    }
}

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
 * @author yaming_deng
 * @date 2013-1-24
 */
public class SimplePooledDataSourceFactoryBean extends DataSourceFactoryBeanMix implements FactoryBean<ShardPooledDataSource>, InitializingBean, DisposableBean  {
	
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
        this.engineType = ObjectUtils.toString(cfg.get("type"));

		BoneCPConfig config = new BoneCPConfig();
		config.setDisableJMX(false);
		config.setUsername(ObjectUtils.toString(cfg.get("user")));
		config.setPassword(ObjectUtils.toString(cfg.get("pwd")));
		config.setProperties(this.jdbcConfig.getPoolProperties());
		config.setStatisticsEnabled(true);
        config.setConnectionHook(getConnectionHook());
		String url = ObjectUtils.toString(cfg.get("url"));
        config.setJdbcUrl(this.getJdbcFullUrl(url));
		this.shardPooledDataSource = new ShardPooledDataSource(config);
        shardPooledDataSource.setDriverClass(this.getDriver());
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

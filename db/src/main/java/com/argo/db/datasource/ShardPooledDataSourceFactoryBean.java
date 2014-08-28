package com.argo.db.datasource;

import com.argo.db.JdbcConfig;
import com.argo.db.shard.ShardDbDef;
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
 * 分布式数据源工厂Bean.
 * 
 * Factory --> ShardPooledDataSource
 *
 * @author yaming_deng
 * @date 2013-1-24
 */
public class ShardPooledDataSourceFactoryBean extends DataSourceFactoryBeanMix implements FactoryBean<ShardPooledDataSource>, InitializingBean, DisposableBean  {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private ShardPooledDataSource shardPooledDataSource;
    private String name;
    private String dbid;

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
        Map holder = JdbcConfig.current.getHolder();
        if (holder == null || holder.size() == 0){
            throw new Exception("jdbc.yaml dones't find any farm configuration. please check.");
        }
        Map temp = JdbcConfig.current.getCommon();
        if(temp == null || temp.size() == 0){
            throw new Exception("jdbc.yaml dones't find any common configuration. please check.");
        }
        this.engineType = ObjectUtils.toString(temp.get("type"));

		BoneCPConfig config = new BoneCPConfig();
		config.setDisableJMX(false);
		config.setUsername(ObjectUtils.toString(temp.get("user")));
		config.setPassword(ObjectUtils.toString(temp.get("pwd")));
		config.setProperties(JdbcConfig.current.getPoolProperties());
		config.setStatisticsEnabled(true);
		config.setConnectionHook(getConnectionHook());

		//业务Shard数据源
        ShardDbDef server = (ShardDbDef) holder.get(this.dbid);
        config.setJdbcUrl(this.getJdbcFullUrl(server.toString()));
        ShardPooledDataSource master = new ShardPooledDataSource(config);
        master.setDriverClass(this.getDriver());
        this.shardPooledDataSource = master;
	}
	

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		this.shardPooledDataSource.close();
	}

    public String getDbid() {
        return dbid;
    }

    public void setDbid(String dbid) {
        this.dbid = dbid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.argo.db.datasource;

import com.argo.db.JdbcConfig;
import com.google.common.collect.Lists;
import com.jolbox.bonecp.BoneCPConfig;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * 
 * 分布式数据源工厂Bean.
 * 
 * Factory --> RoutingDataSource --> DataSource
 *
 * @author yaming_deng
 * @date 2013-1-24
 */
public class MasterSlaveDataSourceFactoryBean extends DataSourceFactoryBeanMix implements FactoryBean<MasterSlaveRoutingDataSource>, InitializingBean, DisposableBean  {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private MasterSlaveRoutingDataSource msDataSource;

    private JdbcConfig jdbcConfig;
    private List servers;
    private String name;
    private String role;

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public MasterSlaveRoutingDataSource getObject() throws Exception {
		return msDataSource;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return MasterSlaveRoutingDataSource.class;
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
        this.jdbcConfig = JdbcConfig.current;
		this.msDataSource = new MasterSlaveRoutingDataSource();
        this.servers = this.jdbcConfig.get(List.class, "ms");
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

		//M-S数据源
        List<DataSource> sourceList = Lists.newArrayList();
		for (int i=0; i<this.servers.size(); i++) {
            Map server = (Map)this.servers.get(i);
            String name = server.get("name") + "";
            if (!this.name.equalsIgnoreCase(name)){
                continue;
            }

            String[] jdbcUrl = ObjectUtils.toString(server.get(this.role)).split(",");
            for (String url : jdbcUrl) {
                config.setJdbcUrl(this.getJdbcFullUrl(url));
                MasterSlaveDataSource master = new MasterSlaveDataSource(config, role);
                master.setDriverClass(this.getDriver());
                master.setRole(role);
                sourceList.add(master);
            }
		}
        this.msDataSource.setName(this.name);
        this.msDataSource.setRole(this.role);
        this.msDataSource.setTargetDataSources(sourceList);
        logger.info("create datasource. name=" + this.name + ", role=" + this.role);
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		this.msDataSource.destroy();
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

package com.argo.db.datasource;

import com.argo.db.DbEngineEnum;
import com.argo.db.JdbcConfig;
import com.argo.db.hooks.MySQLConnectionHook;
import com.argo.db.hooks.OracleConnectionHook;
import com.google.common.collect.Lists;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.hooks.ConnectionHook;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.util.HashMap;
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
public class MasterSlaveDataSourceFactoryBean implements FactoryBean<MasterSlaveRoutingDataSource>, InitializingBean, DisposableBean  {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private MasterSlaveRoutingDataSource msRoutingDataSource;

    private JdbcConfig jdbcConfig;
    private List servers;
    private String engineType;

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public MasterSlaveRoutingDataSource getObject() throws Exception {
		return msRoutingDataSource;
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
		this.msRoutingDataSource = new MasterSlaveRoutingDataSource();
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
		Map<Object, List<DataSource>> dsMap = new HashMap<Object, List<DataSource>>();
		for (int i=0; i<this.servers.size(); i++) {
            Map server = (Map)this.servers.get(i);
            List<DataSource> sourceList = Lists.newArrayList();
            String[] jdbcUrl = ObjectUtils.toString(server.get("master")).split(",");
            for (String url : jdbcUrl) {
                config.setJdbcUrl(this.getJdbcFullUrl(url));
                MasterSlaveDataSource master = new MasterSlaveDataSource(config, "master");
                master.setDriverClass(this.getDriver());
                sourceList.add(master);
            }
            String key = server.get("name") + "-master";
            dsMap.put(key, sourceList);

            sourceList = Lists.newArrayList();
            jdbcUrl = ObjectUtils.toString(server.get("slaves")).split(",");
            for (String url : jdbcUrl) {
                config.setJdbcUrl(this.getJdbcFullUrl(url));
                MasterSlaveDataSource slave = new MasterSlaveDataSource(config, "slave");
                slave.setDriverClass(this.getDriver());
                sourceList.add(slave);
            }
            key = server.get("name") + "-slave";
            dsMap.put(key, sourceList);

			this.logger.info("@@@Init ShardPooledDataSource: {}", config.getJdbcUrl());
		}
		this.msRoutingDataSource.setTargetDataSources(dsMap);
		//初始化
		this.msRoutingDataSource.afterPropertiesSet();
	}
	
	private ConnectionHook getConnectionHook(){
		if(this.engineType.equalsIgnoreCase(DbEngineEnum.ORACLE)){
			return new OracleConnectionHook();
		}else if(this.engineType.equalsIgnoreCase(DbEngineEnum.MYSQL)){
			return new MySQLConnectionHook();
		}
		return null;
	}

    private String getDriver(){
        if(this.engineType.equalsIgnoreCase(DbEngineEnum.ORACLE)){
            return DRIVER_ORACLE;
        }else if(this.engineType.equalsIgnoreCase(DbEngineEnum.MYSQL)){
            return DRIVER_MYSQL;
        }
        return null;
    }

    private String getJdbcFullUrl(String iphost){
        if(this.engineType.equalsIgnoreCase(DbEngineEnum.ORACLE)){
            return String.format(DRIVER_ORACLE, iphost);
        }else if(this.engineType.equalsIgnoreCase(DbEngineEnum.MYSQL)){
            return String.format(DRIVER_URL_MYSQL, iphost);
        }
        return null;
    }

    public static final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
    public static final String DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver";

    public final static String DRIVER_URL_MYSQL = "jdbc:mysql://%s?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
    public final static String DRIVER_URL_ORACLE = "jdbc:oracle:thin:@%s";

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		this.msRoutingDataSource.destroy();
	}

    public JdbcConfig getJdbcConfig() {
        return jdbcConfig;
    }

    public void setJdbcConfig(JdbcConfig jdbcConfig) {
        this.jdbcConfig = jdbcConfig;
    }
}

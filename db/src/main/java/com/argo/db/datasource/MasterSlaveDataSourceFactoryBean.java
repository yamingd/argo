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

	private MasterSlaveRoutingDataSource msDataSource;

    private JdbcConfig jdbcConfig;
    private List servers;
    private String engineType;
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
            return String.format(DRIVER_URL_ORACLE, iphost);
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

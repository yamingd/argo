package com.argo.db;

import com.argo.core.base.BaseBean;
import com.argo.db.datasource.MasterSlaveRoutingDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


/**
 * Created by yaming_deng on 14-7-28.
 */
@Component
public class MasterSlaveJdbcTemplate extends BaseBean {

    public static final String ROLE_SLAVE = "slave";
    public static final String ROLE_MASTER = "master";

    public JdbcTemplate get(String serverName, boolean master){
        String role = ROLE_SLAVE;
        if (master){
            role = ROLE_MASTER;
        }
        String beanName = "DS_" + serverName + "_" + role;
        MasterSlaveRoutingDataSource dataSource = this.applicationContext.getBean(beanName, MasterSlaveRoutingDataSource.class);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.afterPropertiesSet();
        return jdbcTemplate;
    }
}

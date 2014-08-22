package com.argo.db;

import com.argo.db.datasource.MasterSlaveDataSourceHolder;
import com.argo.db.datasource.MasterSlaveRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


/**
 * Created by yaming_deng on 14-7-28.
 */
@Component
public class MasterSlaveJdbcTemplate {

    @Autowired
    @Qualifier("masterSlaveDatasource")
    private MasterSlaveRoutingDataSource routingDataSource;

    public JdbcTemplate get(String serverName, boolean master){
        String role = "slave";
        if (master){
            role = "master";
        }
        MasterSlaveDataSourceHolder holder = new MasterSlaveDataSourceHolder(serverName, role, routingDataSource);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(holder);
        jdbcTemplate.afterPropertiesSet();
        return jdbcTemplate;
    }
}

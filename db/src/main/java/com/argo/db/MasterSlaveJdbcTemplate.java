package com.argo.db;

import com.argo.db.context.MasterSlaveContext;
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
        MasterSlaveContext context = MasterSlaveContext.getContext();
        context.setTargetName(serverName);
        if (master){
            context.setTargetRole("master");
        }else{
            context.setTargetRole("slave");
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(routingDataSource);
        jdbcTemplate.afterPropertiesSet();
        return jdbcTemplate;
    }
}

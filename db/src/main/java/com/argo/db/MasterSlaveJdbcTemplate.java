package com.argo.db;

import com.argo.core.base.BaseBean;
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
        String beanName = "DS_" + serverName + "_" + role + "Jt";
        JdbcTemplate jdbcTemplate = this.applicationContext.getBean(beanName, JdbcTemplate.class);
        return jdbcTemplate;
    }
}

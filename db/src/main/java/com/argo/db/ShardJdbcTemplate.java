package com.argo.db;

import com.argo.core.base.BaseBean;
import com.argo.db.shard.DataShardContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by yaming_deng on 14-8-26.
 */
@Component
public class ShardJdbcTemplate extends BaseBean {


    public JdbcTemplate get(){
        String dbid = DataShardContext.getContext().getDbid();
        String beanName = "DS_" + dbid + "Jt";
        JdbcTemplate jdbcTemplate = this.applicationContext.getBean(beanName, JdbcTemplate.class);
        return jdbcTemplate;
    }

}

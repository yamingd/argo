package com.argo.db.template;

import com.argo.core.base.BaseBean;
import com.argo.db.JdbcConfig;
import com.argo.db.ShardJdbcTemplate;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by yaming_deng on 14-8-28.
 */
public abstract class ServiceShardTemplate extends BaseBean {

    @Autowired
    private ShardJdbcTemplate shardJdbcTemplate;

    private JdbcConfig jdbcConfig;

    protected JdbcTemplate getJt(){
        return shardJdbcTemplate.get();
    }

    protected void update(String tableName, Map<String, Object> args, String pk, Object pkvalue){
        List<Object> params = Lists.newArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(tableName).append(" set ");
        for (String s : args.keySet()) {
            sb.append(s).append("= ? ,");
            params.add(args.get(s));
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append("  where ").append(pk).append(" = ? ");
        params.add(pkvalue);

        this.getJt().update(sb.toString().intern(), params.toArray());
    }

}

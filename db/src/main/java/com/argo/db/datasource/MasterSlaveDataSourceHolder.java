package com.argo.db.datasource;

import org.springframework.jdbc.datasource.AbstractDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by yaming_deng on 14-8-22.
 */
public class MasterSlaveDataSourceHolder extends AbstractDataSource {

    private String name;
    private String role;
    private MasterSlaveRoutingDataSource pool;
    private String key;

    public MasterSlaveDataSourceHolder(String name, String role, MasterSlaveRoutingDataSource pool) {
        this.name = name;
        this.role = role;
        this.pool = pool;
        this.key = name+"-"+role;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = this.pool.determineTargetDataSource(this.key).getConnection();
        if(logger.isDebugEnabled()){
            this.logger.debug("getConnection: [" + conn + "]");
        }
        return conn;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection conn = this.pool.determineTargetDataSource(this.key).getConnection(username, password);
        if(logger.isDebugEnabled()){
            this.logger.debug("getConnection: [" + conn + "]");
        }
        return conn;
    }
}

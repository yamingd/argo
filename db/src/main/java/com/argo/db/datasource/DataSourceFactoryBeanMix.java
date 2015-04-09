package com.argo.db.datasource;

import com.argo.db.DbEngineEnum;
import com.argo.db.hooks.MySQLConnectionHook;
import com.argo.db.hooks.OracleConnectionHook;
import com.jolbox.bonecp.hooks.ConnectionHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yaming_deng on 14-8-26.
 */
public class DataSourceFactoryBeanMix {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String engineType;

    protected ConnectionHook getConnectionHook(){
        if(engineType.equalsIgnoreCase(DbEngineEnum.ORACLE)){
            return new OracleConnectionHook();
        }else if(engineType.equalsIgnoreCase(DbEngineEnum.MYSQL)){
            return new MySQLConnectionHook();
        }
        return null;
    }

    protected String getDriver(){
        if(this.engineType.equalsIgnoreCase(DbEngineEnum.ORACLE)){
            return DRIVER_ORACLE;
        }else if(this.engineType.equalsIgnoreCase(DbEngineEnum.MYSQL)){
            return DRIVER_MYSQL;
        }
        return null;
    }

    protected String getJdbcFullUrl(String iphost){
        if(this.engineType.equalsIgnoreCase(DbEngineEnum.ORACLE)){
            return String.format(DRIVER_URL_ORACLE, iphost);
        }else if(this.engineType.equalsIgnoreCase(DbEngineEnum.MYSQL)){
            return String.format(DRIVER_URL_MYSQL, iphost);
        }
        return null;
    }

    public static final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
    public static final String DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver";

    public final static String DRIVER_URL_MYSQL = "jdbc:mysql://%s?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&failOverReadOnly=false&rewriteBatchedStatements=true";
    public final static String DRIVER_URL_ORACLE = "jdbc:oracle:thin:@%s";
}

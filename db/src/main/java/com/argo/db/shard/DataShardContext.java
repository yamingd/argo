package com.argo.db.shard;

import com.argo.db.JdbcConfig;

import java.io.Serializable;


/**
 * 存放数据库服务器当前状态数据. 是线程安全的.
 *
 * @author yaming_deng
 * @date 2013-1-16
 */
public class DataShardContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7787309065037672609L;
	
	static ThreadLocal<DataShardContext> dbcontext = new ThreadLocal<DataShardContext>();
	
    public static void setContext(DataShardContext context) {
    	dbcontext.set(context);
    }

    public static DataShardContext getContext() {
    	DataShardContext ctx = dbcontext.get();
    	if(ctx == null){
    		ctx = new DataShardContext();
    		dbcontext.set(ctx);
    	}
    	return ctx;
    }
    
    /**
     * 一级Shard
     */
    private Integer shardId;
    /**
     * 客户ID
     */
    private Integer customerId;

    private String dbid;
	/**
	 * 一级Shard
	 * @return the shardId1
	 */
	public Integer getShardId() {
		return shardId;
	}

	/**
	 * @param shardId the shardId to set
	 */
	public void setShardId(Integer shardId) {
		this.shardId = shardId;
        String dbns = JdbcConfig.current.get(String.class, "dbns");
        this.dbid = String.format(dbns, shardId);
	}

	/**
	 * @return the customerId
	 */
	public Integer getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}


    public String getDbid() {
        return dbid;
    }

    public void setDbid(String dbid) {
        this.dbid = dbid;
    }
}

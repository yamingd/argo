package com.argo.db.context;

import com.argo.db.farm.ServerFarm;
import com.argo.db.farm.ShardDbDef;

import java.io.Serializable;


/**
 * 存放数据库服务器当前状态数据. 是线程安全的.
 *
 * @author yaming_deng
 * @date 2013-1-16
 */
public class DatafarmContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7787309065037672609L;
	
	static ThreadLocal<DatafarmContext> dbcontext = new ThreadLocal<DatafarmContext>();
	
    public static void setContext(DatafarmContext context) {
    	dbcontext.set(context);
    }

    public static DatafarmContext getContext() {
    	DatafarmContext ctx = dbcontext.get();
    	if(ctx == null){
    		ctx = new DatafarmContext();
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

	public ShardDbDef getDbDef() {
		return ServerFarm.current.getShard(shardId);
	}
    
}

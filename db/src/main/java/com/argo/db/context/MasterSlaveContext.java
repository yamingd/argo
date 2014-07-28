package com.argo.db.context;

import java.io.Serializable;


/**
 * 存放数据库服务器当前状态数据. 是线程安全的.
 *
 * @author yaming_deng
 * @date 2013-1-16
 */
public class MasterSlaveContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7787309065037672609L;
	
	static ThreadLocal<MasterSlaveContext> dbcontext = new ThreadLocal<MasterSlaveContext>();
	
    public static void setContext(MasterSlaveContext context) {
    	dbcontext.set(context);
    }

    public static MasterSlaveContext getContext() {
    	MasterSlaveContext ctx = dbcontext.get();
    	if(ctx == null){
    		ctx = new MasterSlaveContext();
    		dbcontext.set(ctx);
    	}
    	return ctx;
    }
    
    private String targetName;
    private String targetRole;

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }
}

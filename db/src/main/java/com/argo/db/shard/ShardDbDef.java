package com.argo.db.shard;

/**
 *  数据库链接结构
 *  
 * @author yaming_deng
 * @date 2013-1-17
 */
public class ShardDbDef implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6532617070883288798L;
	
	private String engine;
	private Integer id;
	private String dbIP; //如 : 192.168.0.90:3309:orcl
	private String dbName;
	private String group;
	/**
	 * @param id
	 * @param ipaddr
	 */
	public ShardDbDef(Integer id, String ipaddr) {
		super();
		this.id = id;		
		this.dbIP = ipaddr;
	}
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}


	public void setEngine(String engine) {
		this.engine = engine;
	}

	public String getEngine() {
		return engine;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbIP(String dbIP) {
		this.dbIP = dbIP;
	}

	public String getDbIP() {
		return dbIP;
	}
	
	public String toString(){
		return this.dbIP+"/"+dbName;
	}

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}

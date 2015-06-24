package com.argo.db.dialect;


import com.argo.db.DbEngineEnum;

/**
 * 描述 ：适合2000, SQL 分页，只支持单表分页，表需包含id列
 *
 * @author yaming_deng
 * @date 2012-8-24
 */
public class SqlServerDialect  extends AbstractDialect {
	
	/**
	 * 
	 */
	private static final String driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private boolean limit = true;
	
	public void setLimit(boolean limit) {
		this.limit = limit;
	}

	@Override
	public String getLimitString(String sql, boolean hasOffset) {
		return new StringBuilder(sql.length() + 10).append(sql).insert(
				sql.toLowerCase().indexOf("select") + 6,
				hasOffset ? " limit ? ?" : " top ?").toString();
	}

	@Override
	public String getLimitString(String sql, int skipResults, int maxResults) {
		// 原理:
		// select top $v_page_size id
		// from table1
		// where id > 
		// (select isnull(max(id), 0) from (
		//    select top $v_page_size * (page-1) id from table1 order by id
		//  ))
		// order by id
		
		sql = sql.toLowerCase();
		int fromPos = sql.indexOf("from");
		int wherePos = sql.indexOf("where");
		int orderBy = sql.indexOf("order by");
		
		StringBuffer temp = new StringBuffer();
		
		temp.append("select top ").append(maxResults).append(" id ");
		temp.append(sql.substring(fromPos, orderBy));
		if(wherePos<0){
			temp.append(" where ");
		}else{
			temp.append(" and ");
		}
		temp.append("id > (");
		temp.append("select isnull(max(id), 0) from(");
		temp.append("select top ").append(skipResults).append(" id ").append(sql.substring(fromPos));
		temp.append(") ");
		temp.append(") ");
		temp.append(sql.substring(orderBy));
		
		return temp.toString();
	}

	@Override
	public boolean isLimit() {
		return this.limit;
	}

	/* (non-Javadoc)
	 */
	@Override
	public String getDriverClass() {
		return driverClass;
	}

	/* (non-Javadoc)
	 */
	@Override
	public String getEngineName() {
		return DbEngineEnum.SQLServer2K;
	}

	/* (non-Javadoc)
	 */
	@Override
	public String formatJdbcUrl(String address, String dbname) {
		return String.format("jdbc:sqlserver://%s;databaseName=%s", address, dbname);
	}

	/* (non-Javadoc)
	 */
	@Override
	public String formatUserName(String username, Integer shardId) {
		return username;
	}
}

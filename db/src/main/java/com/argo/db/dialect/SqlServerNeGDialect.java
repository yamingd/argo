package com.argo.db.dialect;


import com.argo.db.DbEngineEnum;

/**
 * 描述 ：适合2005以后版本
 *
 * @author yaming_deng
 * @date 2012-8-24
 */
public class SqlServerNeGDialect extends AbstractDialect {
	
	/**
	 * 
	 */
	protected static final String driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
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
		/*
		 * select top maxResults *
		 * from (
		 * 	   select ROW_NUMBER()OVER(ORDER BY id) as rn, * from table1
		 * )pt
		 * where rn > skipResults
		 * */
		
		sql = sql.toLowerCase().trim();
		int selectPos = sql.indexOf("select");
		int fromPos = sql.indexOf("from");
		int orderBy = sql.indexOf("order by");
		
		StringBuffer temp = new StringBuffer();
		temp.append("select top ").append(maxResults).append(" * ")
			.append(" from (");
		
		temp.append("select ").append(sql.substring(selectPos + 6, fromPos).trim());
		temp.append(" ,ROW_NUMBER()OVER(").append(sql.substring(orderBy).trim()).append(") as rn ");
		temp.append(sql.substring(fromPos, orderBy).trim());
		temp.append(") pt");
		temp.append(" where rn > ").append(skipResults);

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
		return DbEngineEnum.SQLServer;
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

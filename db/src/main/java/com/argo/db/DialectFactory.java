package com.argo.db;

import com.argo.core.base.BaseBean;
import com.argo.db.dialect.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 描述 ：SQL方言工厂
 *
 * @author yaming_deng
 * @date 2012-12-13
 */
@Component("dialectFactory")
public class DialectFactory extends BaseBean {
		
	private HashMap<String, IDialect> cachesDialect = new HashMap<String, IDialect>();

	public IDialect getDialect(String dbEngineName) {
		IDialect temp =  this.cachesDialect.get(dbEngineName);
		if(temp==null){
			if(dbEngineName.equalsIgnoreCase(DbEngineEnum.ORACLE)){
				temp = new OracleDialect();
			}
			else if(dbEngineName.equalsIgnoreCase(DbEngineEnum.MYSQL)){
				temp = new MySQLDialect();
			}
			else if(dbEngineName.equalsIgnoreCase(DbEngineEnum.SQLServer2K)){
				temp = new SqlServerDialect(); //SQL Server 2000
			}
			else if(dbEngineName.equalsIgnoreCase(DbEngineEnum.SQLServer)){
				temp = new SqlServerNeGDialect(); //SQL Server 2005 以上
			}
			temp.setLimit(true);
			this.cachesDialect.put(dbEngineName, temp);
		}
		return temp;
	}
	
	public void addDialect(IDialect dialect){
		this.cachesDialect.put(dialect.getEngineName(), dialect);
	}
}

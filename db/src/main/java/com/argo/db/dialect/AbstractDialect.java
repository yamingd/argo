package com.argo.db.dialect;

import com.argo.core.base.BaseBean;
import com.argo.db.DialectFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Dialect的抽象实现
 *
 * @author yaming_deng
 * @date 2013-1-18
 */
public abstract class AbstractDialect extends BaseBean implements IDialect {
	
	@Autowired
	private DialectFactory dialectFactory;
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.dialectFactory.addDialect(this);
	}
	
	public String formatJdbcUrl(String cfgUrl){
		String str[] = cfgUrl.split("/");
		return this.formatJdbcUrl(str[0], str[1]);
	}
}

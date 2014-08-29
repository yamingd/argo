package com.argo.service.factory;

import com.argo.core.base.BaseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 描述 ：
 *
 * @author yaming_deng
 * @date 2013-1-15
 */
public abstract class ServiceFactoryBase extends BaseBean {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
		
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
	}

}

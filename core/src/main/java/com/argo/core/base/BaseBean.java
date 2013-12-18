package com.argo.core.base;

import com.argo.core.configuration.SiteConfig;
import com.argo.core.service.factory.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 
 * 
 * 
 * @author yaming_deng
 * @date 2013-1-17
 */
public abstract class BaseBean implements InitializingBean, DisposableBean, BeanNameAware{
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String beanName;
	
	@Autowired
	@Qualifier("serviceLocator")
	protected ServiceLocator serviceLocator;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}

	@Override
	public void destroy() throws Exception {
		
	}
	
	public SiteConfig getSiteConfig(){
		return SiteConfig.instance;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/**
	 * Return the bean name that this listener container has been assigned
	 * in its containing bean factory, if any.
	 */
	protected final String getBeanName() {
		return this.beanName;
	}
}

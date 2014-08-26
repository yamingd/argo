package com.argo.core.service.factory;

import com.argo.core.ContextConfig;
import com.argo.core.exception.ServiceNotFoundException;
import com.argo.core.service.ServiceConfig;
import com.argo.core.service.proxy.HttpServiceClientGenerator;
import com.argo.core.service.proxy.RmiServiceClientGenerator;
import com.argo.core.service.proxy.ServiceClientPoolListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Bean服务定位.
 *
 * @author yaming_deng

 */
@Component("serviceLocator")
public class ServiceLocator implements ApplicationContextAware,InitializingBean {
	
	/**
	 * 
	 */
	protected static final String CFG_REMOTE_HTTP = "http://";
	
	protected static final String CFG_REMOTE_RMI = "rmi://";

	public static ServiceLocator instance = null;
	
	private ApplicationContext ctx = null;
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("httpServiceClientGenerator")
	private HttpServiceClientGenerator httpServiceClientGenerator;
	
	@Autowired
	@Qualifier("rmiServiceClientGenerator")
	private RmiServiceClientGenerator rmiServiceClientGenerator;
	
	/* (non-Javadoc)
	 * @see org.springframework.shard.ApplicationContextAware#setApplicationContext(org.springframework.shard.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.ctx = 	applicationContext;
	}
	
	public String getServiceName(Class<?> clazz) {
		String beanName;
		String[] temp = clazz.getName().split("\\.");
		beanName = temp[temp.length-1];
		beanName = beanName.substring(0, 1).toLowerCase()+beanName.substring(1);
		return beanName;
	}
	
	/**
	 * 仅读取本地服务.
	 * @param <T>
	 * @param serviceName
	 * @return
	 * @throws ServiceNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String serviceName){
		Object temp = null;
		try {
			temp = this.ctx.getBean(serviceName);
			if(temp != null){
				return (T) temp;
			}
		} catch (BeansException e) {
			
		}
		try{
			temp = this.ctx.getBean(serviceName+"Impl"); //尝试定位服务的多态实现
			if(temp != null){
				return (T) temp;
			}
		} catch (BeansException e) {
			
		}
		
		this.logger.error("@@Can't find bean, name=" + serviceName);
		
		return null;
	}
	
	private ServiceConfig getConfig(){
		return ServiceConfig.instance;
	}

    private boolean isRemoteAvaliable(){
        boolean flag = ContextConfig.role() == null || ServiceClientPoolListener.instance == null;
        return !flag;
    }

	/**
	 * 读取本地、远程服务
	 * @param <T>
	 * @param clazz
	 * @return
	 * @throws ServiceNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz){
		String serviceName = this.getServiceName(clazz);
        if (!this.isRemoteAvaliable()){
            //本地服务Bean
            return (T) this.get(serviceName);
        }
		String serviceCfg = this.getConfig().getService(serviceName);
		if(StringUtils.isBlank(serviceCfg)){
            serviceCfg = this.getConfig().getServiceType();
            if(StringUtils.isBlank(serviceCfg)){
			    return (T) this.get(serviceName);
            }
		}
		if(serviceCfg.startsWith(CFG_REMOTE_HTTP)){
			//远程服务Bean
			serviceName = serviceCfg.replace(CFG_REMOTE_HTTP, "");
			if(StringUtils.isBlank(serviceName)){
				return (T) this.httpServiceClientGenerator.getService(clazz);
			}else{
				return (T) this.httpServiceClientGenerator.getService(clazz, serviceName);
			}
		}if(serviceCfg.startsWith(CFG_REMOTE_RMI)){
			//远程服务Bean
			serviceName = serviceCfg.replace(CFG_REMOTE_RMI, "");
			if(StringUtils.isBlank(serviceName)){
				return (T) this.rmiServiceClientGenerator.getService(clazz);
			}else{
				return (T) this.rmiServiceClientGenerator.getService(clazz, serviceName);
			}
		}else{
			//本地服务Bean
			return (T) this.get(serviceName);
		}
	}
	
	/**
	 * 读取本地、远程服务
	 * @param <T>
	 * @param clazz
	 * @param serviceName
	 * @return
	 * @throws ServiceNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz, String serviceName){
        if (!this.isRemoteAvaliable()){
            //本地服务Bean
            return (T) this.get(serviceName);
        }

		String serviceCfg = this.getConfig().getService(serviceName);
		if(StringUtils.isBlank(serviceCfg)){
            serviceCfg = this.getConfig().getServiceType();
            if(StringUtils.isBlank(serviceCfg)){
                return (T) this.get(serviceName);
            }
		}
		if(serviceCfg.startsWith(CFG_REMOTE_HTTP)){
			//远程服务Bean
			return (T) this.httpServiceClientGenerator.getService(clazz, serviceName);
		}if(serviceCfg.startsWith(CFG_REMOTE_RMI)){
			//远程服务Bean
			return (T) this.rmiServiceClientGenerator.getService(clazz, serviceName);
		}else{
			//本地服务Bean
			return (T) this.get(serviceName);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		ServiceLocator.instance = this;
        new ServiceConfig().afterPropertiesSet();
	}
	
}

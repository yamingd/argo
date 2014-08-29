package com.argo.service.factory;

import com.argo.core.ContextConfig;
import com.argo.core.exception.ServiceNotFoundException;
import com.argo.core.service.ServiceLocator;
import com.argo.service.ServiceConfig;
import com.argo.service.ServiceMode;
import com.argo.service.proxy.HttpServiceClientGenerator;
import com.argo.service.proxy.RmiServiceClientGenerator;
import com.argo.service.proxy.ServiceClientGenerator;
import com.argo.service.proxy.ServiceClientPoolListener;
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
public class ServiceLocatorImpl implements ApplicationContextAware,InitializingBean, ServiceLocator {

	public static ServiceLocator instance = null;
	
	private ApplicationContext ctx = null;
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("httpServiceClientGenerator")
	private HttpServiceClientGenerator httpServiceClientGenerator;
	
	@Autowired
	@Qualifier("rmiServiceClientGenerator")
	private RmiServiceClientGenerator rmiServiceClientGenerator;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.ctx = 	applicationContext;
	}
	
	@Override
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
	@Override
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

    private ServiceClientGenerator getGenerator(String token){
        if(token.startsWith(HttpServiceClientGenerator.PROTOCOL_HTTP)){
            //远程服务Bean
            return this.httpServiceClientGenerator;
        }if(token.startsWith(RmiServiceClientGenerator.PROTOCOL_RMI)){
            //远程服务Bean
            return this.rmiServiceClientGenerator;
        }
        return null;
    }
	/**
	 * 读取本地、远程服务
	 * @param <T>
	 * @param clazz
	 * @return
	 * @throws ServiceNotFoundException
	 */
	@Override
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
            if(StringUtils.isBlank(serviceCfg) || ServiceMode.Local.equalsIgnoreCase(serviceCfg)){
			    return (T) this.get(serviceName);
            }
		}
        ServiceClientGenerator gen = this.getGenerator(serviceCfg);
        if (gen != null){
            //远程服务
            serviceName = gen.stripServiceName(serviceCfg);
            if(StringUtils.isBlank(serviceName)){
                return (T) gen.getService(clazz);
            }else{
                return (T) gen.getService(clazz, serviceName);
            }
        }
        //本地服务Bean
        return (T) this.get(serviceName);
	}
	
	/**
	 * 读取本地、远程服务
	 * @param <T>
	 * @param clazz
	 * @param serviceName
	 * @return
	 * @throws ServiceNotFoundException
	 */
	@Override
    @SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz, String serviceName){
        if (!this.isRemoteAvaliable()){
            //本地服务Bean
            return (T) this.get(serviceName);
        }

		String serviceCfg = this.getConfig().getService(serviceName);
		if(StringUtils.isBlank(serviceCfg)){
            serviceCfg = this.getConfig().getServiceType();
            if(StringUtils.isBlank(serviceCfg) || ServiceMode.Local.equalsIgnoreCase(serviceCfg)){
                return (T) this.get(serviceName);
            }
		}

        ServiceClientGenerator gen = this.getGenerator(serviceCfg);
        if (gen != null){
            //远程服务
            if(StringUtils.isBlank(serviceName)){
                return (T) gen.getService(clazz);
            }else{
                return (T) gen.getService(clazz, serviceName);
            }
        }

        //本地服务Bean
        return (T) this.get(serviceName);
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		ServiceLocatorImpl.instance = this;
        if (ServiceConfig.instance == null) {
            new ServiceConfig().afterPropertiesSet();
        }
	}
	
}

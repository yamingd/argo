package com.argo.core.service.beans;

import com.argo.core.ContextConfig;
import com.argo.core.service.annotation.RmiService;
import com.argo.core.service.factory.ServiceNameBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.remoting.rmi.RmiServiceExporter;

/**
 * 根据RmiService来自动创建RmiServiceExporter
 *
 * @author yaming_deng
 * @date 2013-1-11
 */
public class RemotingBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
	 */
	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
		if (!(beanFactory instanceof DefaultListableBeanFactory)) {
			throw new IllegalStateException(
					"CustomAutowireConfigurer needs to operate on a DefaultListableBeanFactory");
		}
		
		DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
		
		wrapRmiService(dlbf);
	}

	private void wrapRmiService(DefaultListableBeanFactory dlbf) {
		
		String[] beanNames = dlbf.getBeanDefinitionNames();
		
		for (String beanName : beanNames) {
			BeanDefinition def = dlbf.getBeanDefinition(beanName);
			if(def.getBeanClassName()==null || !(def instanceof AbstractBeanDefinition)){
				continue;
			}
			try {
				Class<?> beanClass = Class.forName(def.getBeanClassName());
				this.postAddBean(dlbf, beanName, beanClass);
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}

	private void postAddBean(DefaultListableBeanFactory dlbf, String beanName,
			Class<?> clzz) {
		
		RmiService annotation = clzz.getAnnotation(RmiService.class);
		
		if(annotation==null){
			return;
		}

        String rmi = ContextConfig.get("rmi");
        if (!"true".equalsIgnoreCase(rmi)){
            return;
        }

		String serviceName = ServiceNameBuilder.get(annotation.serviceInterface(), annotation.servcieName());
		
		logger.info("@@@wrapRmiService-postProcessBeanFactory0, beanName=" + serviceName);
		
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(RmiServiceExporter.class.getName());
		
		builder.addPropertyValue("serviceInterface", annotation.serviceInterface());
		builder.addPropertyReference("service", beanName);
		builder.addPropertyValue("serviceName", serviceName);
		builder.addPropertyValue("registryPort", annotation.port());
		builder.addPropertyValue("replaceExistingBinding", annotation.replaceExistingBinding());
		builder.addPropertyValue("alwaysCreateRegistry", annotation.alwaysCreateRegistry());
		
		dlbf.registerBeanDefinition(serviceName+"Proxy", builder.getBeanDefinition());
		
		RemotingServiceBeanManager.add(beanName, serviceName);
		
		logger.info("@@@wrapRmiService-postProcessBeanFactory1, beanName=" + serviceName);
	}

}

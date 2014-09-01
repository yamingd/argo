package com.argo.service.beans;

import com.argo.core.ContextConfig;
import com.argo.service.annotation.RmiService;
import com.argo.service.factory.ServiceNameBuilder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.remoting.rmi.RmiServiceExporter;

/**
 * 在服务端.
 * 根据RmiService来自动创建RPC Service.
 *
 *
 * @author yaming_deng
 * @date 2013-1-11
 */
public class RpcServiceBeanFactoryPostProcessor extends ServiceBeanFactoryPostProcessor {

    @Override
	protected void postAddBean(DefaultListableBeanFactory dlbf, String beanName,
			Class<?> clzz) {
		
		RmiService annotation = clzz.getAnnotation(RmiService.class);
		
		if(annotation==null){
			return;
		}

        if (!ContextConfig.isRmiEnabled()){
            return;
        }

		String serviceName = ServiceNameBuilder.get(annotation.serviceInterface(), annotation.servcieName());
		
		logger.info("@@@wrapRmiService-postProcessBeanFactory0, beanName=" + beanName + ", serviceName=" + serviceName);
		
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(RmiServiceExporter.class.getName());
		
		builder.addPropertyValue("serviceInterface", annotation.serviceInterface());
		builder.addPropertyReference("service", beanName);
		builder.addPropertyValue("serviceName", serviceName);
		builder.addPropertyValue("registryPort", annotation.port());
		builder.addPropertyValue("replaceExistingBinding", annotation.replaceExistingBinding());
		builder.addPropertyValue("alwaysCreateRegistry", annotation.alwaysCreateRegistry());
		
		dlbf.registerBeanDefinition(serviceName+"Proxy", builder.getBeanDefinition());
		
		ServiceBeanManager.add(beanName, serviceName);
		
		logger.info("@@@wrapRmiService-postProcessBeanFactory1, beanName=" + beanName + ", serviceName=" + serviceName);
	}

}

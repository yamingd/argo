package com.argo.service.beans;

import com.argo.core.ContextConfig;
import com.argo.service.ServiceConfig;
import com.argo.service.annotation.RmiService;
import com.argo.service.factory.ServiceNameBuilder;
import com.argo.service.proxy.ServiceProxyWireBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.List;

/**
 * 在服务端.
 * 根据RmiService来自动创建RPC Service.
 *
 * @author yaming_deng
 * @date 2013-1-11
 */
public class RpcServiceClientBeanFactoryPostProcessor extends ServiceBeanFactoryPostProcessor {

    private List<String> rmiDisabled;

    public RpcServiceClientBeanFactoryPostProcessor() {
        super();
        rmiDisabled = ServiceConfig.instance.get(List.class, "rmi_disabled");
    }

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

        if (rmiDisabled.contains(serviceName)){
            return;
        }


		logger.info("@@@RpcServiceClientBeanFactoryPostProcessor, serviceName=" + serviceName + ", beanName=" + beanName);
		
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ServiceProxyWireBeanFactory.class.getName());
		
		builder.addPropertyValue("serviceClass", annotation.serviceInterface());
        builder.addPropertyValue("serviceName", serviceName);
		builder.addPropertyReference("localService", beanName);
        builder.addPropertyReference("generator", "rmiServiceClientGenerator");
		
		dlbf.registerBeanDefinition(serviceName, builder.getBeanDefinition());

        logger.info("@@@RpcServiceClientBeanFactoryPostProcessor, serviceName=" + serviceName + ", beanName=" + beanName);

	}

}

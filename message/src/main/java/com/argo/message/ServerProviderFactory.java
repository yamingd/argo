package com.argo.message;

import com.argo.core.base.BaseBean;
import com.argo.core.service.factory.ServiceLocator;
import com.argo.message.server.ServerProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


/**
 *
 * @author yaming_deng
 */
@Component("serverProviderFactory")
public class ServerProviderFactory extends BaseBean {

	public ServerProvider getOne() throws Exception{
		String serviceName = MessageConfig.current.getProvider();
		return this.getOne(serviceName);
	}
	
	public ServerProvider getOne(String serviceName) throws Exception{
		if(StringUtils.isBlank(serviceName)){
			throw new Exception("请配置MQ Server Provider. values=activeMQ or rabbitMQ！");
		}
		
		ServerProvider serverProvider = ServiceLocator.instance.get(serviceName);
		if(serverProvider == null){
			throw new Exception("没有找到可用的MQ Server Provider.");
		}
		
		return serverProvider;
	}
}

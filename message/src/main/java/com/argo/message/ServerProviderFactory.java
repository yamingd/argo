package com.argo.message;

import com.argo.core.base.BaseBean;
import com.argo.message.server.ServerProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


/**
 *
 * @author yaming_deng
 */
@Component("serverProviderFactory")
public class ServerProviderFactory extends BaseBean {

    private MessageConfig messageConfig;

	public ServerProvider getOne() throws Exception{
		String serviceName = messageConfig.getProvider();
		return this.getOne(serviceName);
	}
	
	public ServerProvider getOne(String serviceName) throws Exception{
		if(StringUtils.isBlank(serviceName)){
			throw new Exception("请配置MQ Server Provider. values=activeMQ or rabbitMQ！");
		}
		
		ServerProvider serverProvider = this.applicationContext.getBean(serviceName, ServerProvider.class);
		if(serverProvider == null){
			throw new Exception("没有找到可用的MQ Server Provider.");
		}
		
		return serverProvider;
	}

    @Override
    public void afterPropertiesSet() throws Exception {
        if (MessageConfig.current == null){
            messageConfig = new MessageConfig();
            messageConfig.afterPropertiesSet();
        }else{
            messageConfig = MessageConfig.current;
        }

        //init metrics

    }
}

package com.argo.message;

import com.argo.core.base.BaseBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * MQ消息处理类管理者.
 * @author yaming_deng
 *
 */
@Component("mqQueueConsumerManager")
public class MQQueueConsumerManager extends BaseBean {

	private List<MQMessageConsumer> cosumers = null;

    private MessageConfig messageConfig;

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		// 初始化队列监听.
		try {
            if (MessageConfig.current == null){
                messageConfig = new MessageConfig();
                messageConfig.afterPropertiesSet();
            }else{
                messageConfig = MessageConfig.current;
            }
			boolean flag = messageConfig.get(Boolean.class, "enable");
			if(!flag){
				return;
			}
			
			cosumers = new ArrayList<MQMessageConsumer>();
			
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void destroy() throws Exception {
		
	}
	
	public void addCosumer(MQMessageConsumer item) {
		this.cosumers.add(item);
	}

	public List<MQMessageConsumer> getCosumers() {
		return cosumers;
	}

}

package com.argo.message;

import com.argo.core.base.BaseBean;
import com.argo.message.server.ServerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

/**
 * MQ消息发送基类.
 * @author yaming_deng
 *
 */
public abstract class AbstractMessageProducerBase extends BaseBean {
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	private ServerProvider serverProvider;
	
	@Autowired
	@Qualifier("serverProviderFactory")
	private ServerProviderFactory serverProviderFactory;
	

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		this.serverProvider = this.serverProviderFactory.getOne(this.getProviderName());
		Assert.notNull(this.getServerProvider(), "serverProvider can't be NULL.");
		this.getServerProvider().initialize();
	}
	
	/**
	 * 返回队列名.
	 * @return
	 */
	protected abstract String getDestinationName();
	
	/**
	 * 在子类调用本方法发送消息到MQ服务器.
	 * @throws Exception
	 */
	
	protected String getFullQueueName() {
		String Q_PREFIX = MessageConfig.current.getNS();
		String qname = Q_PREFIX+":"+this.getDestinationName();
		return qname;
	}
	
	protected void sendMessage(MessageEntity message) throws MessageException{
		String qname = getFullQueueName();
		this.sendMessage(qname, message);
	}
	
	protected void sendMessage(String qname, MessageEntity message) throws MessageException{
		message.setFromAppName((String)this.getSiteConfig().getApp().get("name"));
		message.setToQueueName(qname);
		message.checkData();
		this.getServerProvider().publish(qname, message);
	}
	
	/**
	 * 重写这个方法，可以定制要使用的MQ服务器
	 * @return
	 */
	protected String getProviderName(){
		return MessageConfig.current.getProvider();
	}
	
	protected void sendMessage(String qname, String JsonMessage) throws MessageException{
		this.getServerProvider().publish(qname, JsonMessage);
	}

	protected ServerProvider getServerProvider() {
		return serverProvider;
	}

}

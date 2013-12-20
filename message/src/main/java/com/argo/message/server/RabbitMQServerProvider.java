package com.argo.message.server;

import com.argo.core.ContextConfig;
import com.argo.core.json.GsonUtil;
import com.argo.core.utils.IpUtil;
import com.argo.message.*;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RabbitMQ实现
 *
 * @author yaming_deng
 * @date 2012-4-19
 */
public class RabbitMQServerProvider extends AbstractServerProvider {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private List<SimpleMessageListenerContainer> containerList = new ArrayList<SimpleMessageListenerContainer>();
	private CachingConnectionFactory connectionFactory = null;
	private RabbitAdmin rabbitAdmin;
	private RabbitTemplate rabbitTemplate;
	private SimpleMessageConverter messageConverter;

    private Map amqpConfig = null;

	@Override
	protected void doShutdown(){
		for (SimpleMessageListenerContainer container : containerList) {
			container.destroy();
		}	
		this.connectionFactory.destroy();
	}

	/* (non-Javadoc)
	 */
	@Override
	public void listenQueue(MQMessageConsumer cos)
			throws MessageException {
		logger.info("@@ listenQueue. queueName = " + cos.getConsumerId());
		this.subscribeQueue(cos);
		logger.info("@@ listenQueue DONE. queueName = " + cos.getConsumerId());
	}
	
	private void subscribeQueue(MQMessageConsumer consumer) 
		throws MessageException {

        String realQName = getIpQueueName(consumer.getDestinationName());

		Integer cc = MessageConfig.current.get(Integer.class, "concurrent");
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setQueueNames(realQName);
		container.setConnectionFactory(connectionFactory);
		container.setConcurrentConsumers(cc);
		
		MessageListenerAdapter apdapter = new MessageListenerAdapter(consumer);
		apdapter.setMessageConverter(this.messageConverter);
		apdapter.setDefaultListenerMethod("handleMessage");
		
		container.setMessageListener(apdapter);
		
		this.bindingQueue(consumer.getConsumerId());
		
		containerList.add(container);
	}

    private String getIpQueueName(String qname){
        if (ContextConfig.isDev()){
            String[] ipaddr = IpUtil.getHostServerIp();
            return ipaddr[0] + qname;
        }
        return qname;
    }

	private void bindingQueue(String qname){
		this.rabbitAdmin.deleteQueue(qname);	
		String realQName = getIpQueueName(qname);
		Binding binding = new Binding(realQName+"_q", Binding.DestinationType.QUEUE, this.getExchangeName(), realQName+"_r", null);
		this.rabbitAdmin.declareBinding(binding);
	}
	
	/* (non-Javadoc)
	 */
	@Override
	public void publish(String qname, MessageEntity message)
			throws MessageException {
		
		try {
			String exchange = getExchangeName();
			String data = GsonUtil.toJson(message);
            if (logger.isDebugEnabled()){
			    logger.debug(data);
            }
            String realq = this.getIpQueueName(qname);
			this.rabbitTemplate.convertAndSend(exchange, realq, data);
            MessageMetric.publishIncr(this.getClass(), qname, message.getOpCode()+"");
		} catch (Exception e) {
			throw new MessageException("发送JMS消息错误. qname="+qname, e);
		}
		
	}

	private String getExchangeName() {
		return ObjectUtils.toString(this.amqpConfig.get("exchange"));
	}

	/* (non-Javadoc)
	 */
	@Override
	public synchronized void initialize() {
		this.amqpConfig = MessageConfig.current.get(Map.class, "amqp");
        Assert.notNull(this.amqpConfig, "Missing RabbitMQ configuration.");
		if(this.connectionFactory!=null){
			//已经初始化了
			return;
		}

		String host = ObjectUtils.toString(this.amqpConfig.get("host"));
		Integer port = Integer.parseInt(ObjectUtils.toString(this.amqpConfig.get("port")));
		String userName = ObjectUtils.toString(this.amqpConfig.get("user"));
		String password = ObjectUtils.toString(this.amqpConfig.get("passwd"));
		String vhost = ObjectUtils.toString(this.amqpConfig.get("vhost"));
		
		Assert.notNull(host, "Missing RabbitMQ connection Host");
		Assert.notNull(port, "Missing RabbitMQ connection Port");
		Assert.notNull(userName, "Missing RabbitMQ connection userName");
		Assert.notNull(password, "Missing RabbitMQ connection Password");
		Assert.notNull(vhost, "Missing RabbitMQ vhost");
		
		connectionFactory = new CachingConnectionFactory(host, port);
		connectionFactory.setUsername(userName);
		connectionFactory.setPassword(password);
		connectionFactory.setVirtualHost(vhost);
		
		this.rabbitAdmin = new RabbitAdmin(connectionFactory);
		this.rabbitAdmin.afterPropertiesSet();
		
		this.rabbitTemplate = new RabbitTemplate(this.connectionFactory);
		this.rabbitTemplate.afterPropertiesSet();
		
		this.messageConverter = new SimpleMessageConverter();
		
		TopicExchange topic = new TopicExchange(getExchangeName());
		this.rabbitAdmin.declareExchange(topic);
	}

	/* (non-Javadoc)
	 */
	@Override
	public void publish(String qname, String jsonMssage)
			throws MessageException {
		try {
			String exchange = getExchangeName();
            String realq = this.getIpQueueName(qname);
			this.rabbitTemplate.convertAndSend(exchange, realq, jsonMssage);
            MessageMetric.publishIncr(this.getClass(), qname, "");
		} catch (Exception e) {
			throw new MessageException("发送JMS消息错误. qname="+qname, e);
		}		
	}

    public String getProviderName() {
        return "rabbitMQ";
    }
}

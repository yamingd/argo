package com.argo.message.server;

import com.argo.message.MQMessageConsumer;
import com.argo.message.MessageEntity;
import com.argo.message.MessageException;

/**
 * 描述 ：
 *
 * @author yaming_deng
 * @date 2012-4-19
 */
public interface ServerProvider {
	
	void initialize();
	/**
	 * 发布消息
	 * @param qname
	 * @param message
	 * @throws MessageException
	 */
	void publish(String qname, MessageEntity message) throws MessageException;
	void publish(String qname, String jsonMssage) throws MessageException;
	/**
	 * 监听队列
	 * @param cosumer
	 */
	void listenQueue(MQMessageConsumer cosumer) throws MessageException;
	
	/**
	 * 销毁服务
	 * @throws Exception
	 */
	void destroy()throws Exception;
}

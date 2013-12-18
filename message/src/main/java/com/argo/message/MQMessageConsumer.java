package com.argo.message;


/**
 * MQ消息处理接口定义.
 * @author yaming_deng
 *
 */
public interface MQMessageConsumer {
	/**
	* 消息来自的MQ服务器TCP连接串，形如tcp://localhost:61617
	* @return
	*/
	String getDestinationUrl();
	/**
	* 消息来自的队列名, 不包括项目名. 
	* @return
	*/
	String getDestinationName();
	
	/**
	 * 消息队列的全名,包括项目名.
	 * @return
	 */
	String getConsumerId();

    /**
     * 处理JSON消息
     * @param message
     * @throws Exception
     */
	void handleMessage(String message) throws Exception;
	/**
	* 消费消息.
	* 
	* @param message
	*/
	void handleMessage(MessageEntity message) throws Exception;

	/**
	 * 是否处理本项目自己产生的消息.
	 * @return
	 */
	boolean isLocal();
}

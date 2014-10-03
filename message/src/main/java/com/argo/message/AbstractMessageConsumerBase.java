package com.argo.message;

import com.argo.core.base.BaseBean;
import com.argo.core.json.JsonUtil;
import com.argo.message.server.ServerProvider;
import com.codahale.metrics.Timer;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.SchedulingException;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.Date;
import java.util.Map;

/**
 * MQ消息处理基类.
 * @author yaming_deng
 *
 */
public abstract class AbstractMessageConsumerBase extends BaseBean
	implements MessageListener, MQMessageConsumer {
	
	/**
	 * 
	 */
	private String destinationUrl;
	/**
	 * 监听的队列名.
	 */
	private String destinationName;
	
	private boolean local = true;
	
	private ServerProvider serverProvider;
	
	@Autowired
	@Qualifier("serverProviderFactory")
	private ServerProviderFactory serverProviderFactory;
	
	@Autowired
	@Qualifier("mqQueueConsumerManager")
	private MQQueueConsumerManager mqQueueConsumerManager;

    private MessageMetric messageMetric;

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
        messageMetric = new MessageMetric(this.getClass(), this.getDestinationName());
		if(doCheck()){
			serverProvider = serverProviderFactory.getOne(this.getProviderName());
			serverProvider.initialize();
			this.mqQueueConsumerManager.addCosumer(this);
			this.startUp();
		}
	}
	
	/**
	 * 重写这个方法，可以定制要使用的MQ服务器
	 * @return
	 */
	protected String getProviderName(){
		return MessageConfig.current.getProvider();
	}
	
	private boolean doCheck() {
		//全站监听被屏蔽
        String flag = MessageConfig.current.get(String.class, "enable");
        if(!"true".equalsIgnoreCase(flag)){
            return false;
        }
		//队列被屏蔽
        Map qs = MessageConfig.current.getQueues();
		flag = ObjectUtils.toString(qs.get(this.getDestinationName()));
		if("false".equalsIgnoreCase(flag)){
			return false;
		}		
		return true;
	}
	
	@Override
	public void destroy() throws Exception {
		
	}
	
	private void doHook() throws MessageException {
		serverProvider.listenQueue(this);
        logger.info("Queue Listen Done. queue[{}]", this.getDestinationName());
	}
	
	private void startUp() throws MessageException {
		Thread schedulerThread = new Thread() {
			public void run() {
				try {
					Thread.sleep(60 * 1000);
				}
				catch (InterruptedException ex) {
					// simply proceed
				}
				try {
					doHook();
				}
				catch (Exception ex) {
					throw new SchedulingException("MessageConsumer", ex);
				}
			}
		};
		schedulerThread.setName("MessageConsumer["+this.getDestinationName()+"]");
		schedulerThread.start();
	}
	
	/* (non-Javadoc)
	 */
	public void handleMessage(String message) throws Exception{
		Date start = new Date();

        MessageEntity msg = null;
        try {
            msg = JsonUtil.asT(MessageEntity.class, message);
        } catch (Exception e) {
            messageMetric.consumeFailedIncr("parseJson:"+e.getClass().getSimpleName());
            this.logger.error("Message GsonUtil Errro. content=" + message);
            return;
        }
        Timer.Context context = messageMetric.consumerTimer(msg.getOpCode()+":ts");
        try {
			this.logger.info("@@@start handling message, opCode = " + msg.getOpCode());
			this.handleMessage(msg);
            messageMetric.consumeIncr(msg.getOpCode()+"");
			this.logger.info("@@@finish handle message, opCode = " + msg.getOpCode());
		} catch (Exception e) {
            if(msg!=null){
                messageMetric.consumeFailedIncr(e.getClass().getSimpleName());
            }else{
                messageMetric.consumeFailedIncr(e.getClass().getSimpleName());
            }
			throw e;
		}finally{
            context.stop();
			Long duration = new Date().getTime() - start.getTime();
			duration = duration / 1000;
			if(duration>5){
				this.logger.info("@@@handle Message Too Long, duration={}s, queue={}", duration, this.getDestinationName());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	public void onMessage(Message message) {
		try {
			
			TextMessage txtMsg = (TextMessage)message;
			if(StringUtils.isBlank(txtMsg.getText())){
				logger.error("接收到空白消息.");
				return;
			}
			
			this.handleMessage(txtMsg.getText());
			
		} catch (Exception e) {
			logger.error("消息处理出现错误", e);
		}
	}

	/* (non-Javadoc)
	 */
	public abstract String getDestinationName();
	
	public String getConsumerId(){
		if(this.isLocal()){
			return MessageConfig.current.getNS()+":"+this.getDestinationName();
		}
		return this.getDestinationName();
	}

	public void setDestinationName(String name) {
		this.destinationName = name;
	}

	public void setDestinationUrl(String destinationUrl) {
		this.destinationUrl = destinationUrl;
	}

	public String getDestinationUrl() {
		return destinationUrl;
	}

	public void setLocal(boolean isLocal) {
		this.local = isLocal;
	}

	public boolean isLocal() {
		return local;
	}

}

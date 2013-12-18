package com.argo.elasticsearch.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.argo.elasticsearch.ElasticSearchConfig;

/**
 * An abstract {@link FactoryBean} used to create an ElasticSearch object.
 * @author yaming_deng
 */
public abstract class ElasticsearchAbstractFactoryBean {

    protected final Log logger = LogFactory.getLog(getClass());

    protected ElasticSearchConfig config;

	protected boolean async = false;

	protected ThreadPoolTaskExecutor taskExecutor;


	/**
	 * Enable async initialization
	 *
	 * @param async
	 */
	public void setAsync(boolean async) {
		this.async = async;
	}

	/**
	 * Executor for async init mode
	 *
	 * @param taskExecutor
	 */
	public void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
    }

	/**
	 * Configuration of transport, nodes, alias and so on.
	 * @param config
	 */
	public void setConfig(ElasticSearchConfig config) {
		this.config = config;
	}
}

package com.argo.cluster;

import com.argo.cluster.server.ZookeeperConfig;
import com.argo.cluster.server.ZookeeperServer;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;

public abstract class ZKClientBeanBase implements Watcher, InitializingBean, DisposableBean {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected ZookeeperServer zkServer;
	private boolean isClosing = false;
	private String rootPath = "/root";
	
	@Override
	public void process(WatchedEvent event) {
		if(this.isClosing){
			return;
		}
        if (event.getType() == Event.EventType.None) {
			if(event.getState().equals(Event.KeeperState.SyncConnected)){
				logger.info("zookeeper connection is stable.");
				this.onConnected();
			}else if(event.getState().equals(Event.KeeperState.Disconnected)){
				logger.warn("zookeeper connection is closed.");
				try {
					this.zkServer.reconnect(this);
				} catch (IOException e) {
					logger.warn("zookeeper reconnect error..", e);
				}
			}else if(event.getState().equals(Event.KeeperState.Expired)){
				logger.warn("zookeeper connection is expired.");
				try {
					this.zkServer.reconnect(this);
				} catch (IOException e) {
					logger.warn("zookeeper reconnect error..", e);
				}
			}
        }else {
        	String path = event.getPath();
            if (path != null) {
                // Something has changed on the node, let's find out
            	logger.warn("Node changed on:" + path);
                this.onNodeChanged(event);
            }
        }
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		ZookeeperConfig config = ZookeeperConfig.instance;
		if(config==null){
			config = new ZookeeperConfig();
            config.afterPropertiesSet();
		}
		if(config.getRootPath() != null){
			this.rootPath = config.getRootPath();
		}
		this.zkServer = new ZookeeperServer(config);
		this.zkServer.connect(this);
	}
	
	@Override
	public void destroy() throws Exception{
		logger.warn("It's about to shutdown zookeeper connection.");
		this.isClosing = true;
		this.zkServer.shutdown();
	}
	/**
	 * 连接Zookeeper成功后.
	 */
	protected abstract void onConnected();
	
	/**
	 * 当Node发生变化后.
	 * @param event
	 */
	protected abstract void onNodeChanged(WatchedEvent event);
	
	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
}

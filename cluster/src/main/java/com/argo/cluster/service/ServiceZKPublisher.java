package com.argo.cluster.service;

import com.argo.cluster.ZKClientBeanBase;
import com.argo.core.ContextConfig;
import com.argo.service.beans.ServiceBeanManager;
import com.argo.service.listener.ServicePublishListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

/**
 * 
 * RMI service init --> publish --> zookeeper --> push --> client
 * 
 * find(ServiceZKPublisher)-->publish()
 * 
 * @author yaming_deng
 *
 */
public class ServiceZKPublisher extends ZKClientBeanBase implements ServicePublishListener, ApplicationListener<ContextRefreshedEvent> {
	
	protected static final String FOLDER_SERVICES = "/services";

    public static ServiceZKPublisher instance = null;

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
        this.setRootPath(this.getRootPath() + "/" + ContextConfig.getRunning()+FOLDER_SERVICES);
        ServiceZKPublisher.instance = this;
	}
	
	@Override
	protected void onConnected() {
		this.logger.info("Zookeeper connected, path={}", this.getRootPath());
        ServiceBeanManager.publish();
	}

	@Override
	protected void onNodeChanged(WatchedEvent event) {
		this.logger.info("onNodeChanged, type={}, path={}", event.getType(), event.getPath());
	}

	@Override
	public boolean publish(String name, String url) {
		String path = this.getRootPath() + "/" + name;
		this.logger.info("Publishing Service, name={}, url={}", name, url);
		try {
			this.zkServer.createFolderNodes(path, CreateMode.PERSISTENT);
			this.zkServer.createDataNode(path + "/node", url.getBytes(), CreateMode.EPHEMERAL_SEQUENTIAL);
			this.zkServer.watch(path, this);
			this.logger.info("Service Published, name={}, url={}", name, url);
			return true;
		} catch (KeeperException e) {
			this.logger.error("Publish Service Error.", e);
		} catch (InterruptedException e) {
			this.logger.error("Publish Service Error.", e);
		}
		return false;
	}

	@Override
	public boolean remove(String name, String url) {
		String path = this.getRootPath() + "/" + name;
		try {
			List<String> nodes = this.zkServer.getZK().getChildren(path, false);
			for (String node : nodes) {
				byte[] data = this.zkServer.getZK().getData(path +"/" + node, false, null);
				if(url.equalsIgnoreCase(new String(data))){
					this.zkServer.getZK().delete(path +"/" + node, -1);
					break;
				}
			}
			this.logger.info("Service Removed, name={}, url={}", name, url);
			return true;
		} catch (KeeperException e) {
			this.logger.error("Remove Service Error.", e);
		} catch (InterruptedException e) {
			this.logger.error("Remove Service Error.", e);
		}
		return false;
	}

	@Override
	public boolean remove(String name) {
		String path = this.getRootPath() + "/" + name;
		try {
			this.zkServer.getZK().delete(path, -1);
			this.logger.info("Service Removed, name={}, url=ALL", name);
			return true;
		} catch (InterruptedException e) {
			this.logger.error("Remove Service Error.", e);
		} catch (KeeperException e) {
			this.logger.error("Remove Service Error.", e);
		}
		return false;
	}

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info(event.toString());
        ServiceBeanManager.publish();
    }
}

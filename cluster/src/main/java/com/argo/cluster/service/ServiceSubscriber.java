package com.argo.cluster.service;

import com.argo.cluster.ZKClientBeanBase;
import com.argo.core.ContextConfig;
import com.argo.core.service.ServicePoolListener;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class ServiceSubscriber extends ZKClientBeanBase {
	
	protected static final String FOLDER_SERVICES = "/services";
	private ServicePoolListener servicePoolListener;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(servicePoolListener, "property servicePoolListener is NULL.");
		super.afterPropertiesSet();
        this.setRootPath(this.getRootPath()+"/"+ ContextConfig.getRunning()+FOLDER_SERVICES);
	}
	
	@Override
	protected void onConnected() {
		this.logger.info("Zookeeper connected, path={}", this.getRootPath());
		//read services
		try {
			List<String> services = this.zkServer.getZK().getChildren(getRootPath(), this);
			for (String name : services) {
				readServiceNodes(name);
			}
		} catch (KeeperException e) {
			this.logger.error("onConnected Error.", e);
		} catch (InterruptedException e) {
			this.logger.error("onConnected Error.", e);
		}
	}

	protected void readServiceNodes(String name) throws KeeperException,
			InterruptedException {
		String tpath = this.getRootPath() + "/" + name;
		this.logger.info("Reading Service Nodes, path={}", tpath);
		List<String> urls = new ArrayList<String>();
		List<String> childs = this.zkServer.getZK().getChildren(tpath, this);
		for(String node : childs){
			tpath = tpath + "/" + node;
			byte[] data = this.zkServer.getZK().getData(tpath, false, null);
			urls.add(new String(data));
		}
		this.logger.info("Read Service and Notify Service Pool Listener, name={}, urls={}", name, urls);
		this.servicePoolListener.onServiceChanged(name, urls);
	}

	@Override
	protected void onNodeChanged(WatchedEvent event) {
		String path = event.getPath();
		try {
			if(event.getType() == EventType.NodeCreated){
				onServiceReload(path);
			}else if(event.getType() == EventType.NodeDataChanged){
				onServiceReload(path);
			}else if(event.getType() == EventType.NodeDeleted){
				onServiceDeleted(path);
			}else if(event.getType() == EventType.NodeChildrenChanged){
				onServiceReload(path);
			}
		} catch (KeeperException e) {
			this.logger.error("onNodeChanged Error.", e);
		} catch (InterruptedException e) {
			this.logger.error("onNodeChanged Error.", e);
		}
	}
	protected void onServiceReload(String path) throws KeeperException, InterruptedException{
		this.logger.info("onServiceReload, path={}", path);
		String[] temp = path.split(FOLDER_SERVICES);
		if(temp[temp.length-1].length() == 0){
			return;
		}
		String name = temp[temp.length-1].split("/")[0];
		this.readServiceNodes(name);
	}
	
	/**
	 * 同步最新数据到本地.
	 * @param path
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	protected void onServiceDeleted(String path) throws KeeperException,
			InterruptedException {
		this.logger.info("onServiceDeleted, path={}", path);
		String[] temp = path.split(FOLDER_SERVICES);
		if(temp[temp.length-1].length() == 0){
			return;
		}
		String name = temp[temp.length-1].split("/")[0];
		this.readServiceNodes(name);
	}
	
	public ServicePoolListener getServicePoolListener() {
		return servicePoolListener;
	}

	public void setServicePoolListener(ServicePoolListener servicePoolListener) {
		this.servicePoolListener = servicePoolListener;
	}

}

package com.argo.cluster.app;

import com.argo.core.ContextConfig;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;

import com.argo.cluster.ZKClientBeanBase;
import com.argo.cluster.server.ZookeeperConfig;

/**
 * 注册应用运行实例(如Jetty,Tomcat)
 * @author yaming_deng
 *
 */
public class AppNodeRegister extends ZKClientBeanBase {
	
	public static final String FOLDER_CONFIGS = "/servers";
	
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
        this.setRootPath(this.getRootPath()+"/"+ ContextConfig.getRunning()+FOLDER_CONFIGS);
	}
	
	@Override
	protected void onConnected() {
		String path = this.getRootPath() + "/" + ZookeeperConfig.instance.getAppName();
		try {
			this.zkServer.createFolderNodes(path, CreateMode.PERSISTENT);
			this.zkServer.createDataNode(path + "/node", ZookeeperConfig.instance.getAppURI().getBytes(), CreateMode.EPHEMERAL_SEQUENTIAL);
		} catch (KeeperException e) {
			this.logger.error("Add Node Error. path="+path, e);
		} catch (InterruptedException e) {
			this.logger.error("Add Node Error. path="+path, e);
		}
	}

	@Override
	protected void onNodeChanged(WatchedEvent event) {
		this.logger.warn("onNodeChanged. event={}", event);
	}

}

package com.argo.cluster.configuration;

import com.argo.cluster.ZKClientBeanBase;
import com.argo.core.ContextConfig;
import com.argo.core.configuration.ConfigYamlManager;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppConfigWatcher extends ZKClientBeanBase{
	
	public static final String FOLDER_CONFIGS = "/configs";
	

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
        this.setRootPath(this.getRootPath()+"/"+ ContextConfig.getRunning()+FOLDER_CONFIGS);
        pullConfig();
	}


	@Override
	protected void onConnected() {

	}

	/**
	 * 初始化加载配置.
	 */
	protected void pullConfig() {
		String path = this.getRootPath();
		String nodePath = "";
		try {
			List<String> cfs = this.zkServer.getZK().getChildren(path, this);
			for (String name : cfs) {
				String subPath = path + "/" + name;  // component
				List<String> childs = this.zkServer.getZK().getChildren(subPath, this);
				Map<String, Object> config = new HashMap<String, Object>();
				for(String key : childs){
					nodePath = subPath + "/" + key;
					Object value = readConfigValue(nodePath);
					config.put(key, value);
				}
                ConfigYamlManager.add(name, config);
			}
		} catch (KeeperException e) {
			this.logger.error("pullConfig Error. path="+nodePath, e);
		} catch (InterruptedException e) {
			this.logger.error("pullConfig Error. path="+nodePath, e);
		}
	}

	/**
	 * 读取数据.
	 * @param path
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	protected Object readConfigValue(String path) throws KeeperException, InterruptedException {
		return this.zkServer.getNodeData(path, this);
	}

	@Override
	protected void onNodeChanged(WatchedEvent event) {
		String path = event.getPath();
		try {
			if(event.getType() == EventType.NodeCreated){
				
			}else if(event.getType() == EventType.NodeDataChanged){
				onNodeDataChanged(path);
			}else if(event.getType() == EventType.NodeDeleted){
				
			}else if(event.getType() == EventType.NodeChildrenChanged){
				
			}
		} catch (KeeperException e) {
			this.logger.error("onNodeChanged Error. path="+path, e);
		} catch (InterruptedException e) {
			this.logger.error("onNodeChanged Error. path="+path, e);
		}
	}

	/**
	 * 同步最新数据到本地.
	 * @param path
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	protected void onNodeDataChanged(String path) throws KeeperException,
			InterruptedException {
		this.logger.info("onNodeDataChanged. path=" + path);
		String[] temp = path.split("/");
		String key = temp[temp.length-1];
		String name = temp[temp.length-2];
		Object value = this.readConfigValue(path);
		
		this.logger.info("Notify ConfigListener. name="+name);
        ConfigYamlManager.set(name, key, value);
	}

}

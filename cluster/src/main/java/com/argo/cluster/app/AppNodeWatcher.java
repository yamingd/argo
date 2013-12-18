package com.argo.cluster.app;

import com.argo.cluster.ZKClientBeanBase;
import com.argo.cluster.server.ZookeeperConfig;
import com.argo.core.ContextConfig;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 跟踪应用运行实例(如Jetty,Tomcat)
 * @author yaming_deng
 *
 */
public class AppNodeWatcher extends ZKClientBeanBase {
	
	public static final String FOLDER_CONFIGS = "/apps";
	private Map<String, String> nodes = new HashMap<String, String>();
	
	private AppNodeListener listener = null;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
        this.setRootPath(this.getRootPath()+"/"+ ContextConfig.getRunning()+FOLDER_CONFIGS);
	}
	
	@Override
	protected void onConnected() {
		String path = this.getRootPath() + "/" + ZookeeperConfig.instance.getAppName();
		try {
			List<String> childs = this.zkServer.getZK().getChildren(path, this);
			for (String node : childs) {
				this.cacheNode(path + "/" + node, node);
			}
		} catch (KeeperException e) {
			this.logger.error("AppNodeWatcher load nodes error. path="+path, e);
		} catch (InterruptedException e) {
			this.logger.error("AppNodeWatcher load nodes error. path="+path, e);
		}
	}
	
	private String cacheNode(String path, String node) throws KeeperException, InterruptedException{
		if(node.equalsIgnoreCase("apps")){
			return null;
		}
		byte[] data = this.zkServer.getZK().getData(path, this, null);
		String uri = new String(data);
		nodes.put(node, uri);
		return uri;
	}
	
	@Override
	protected void onNodeChanged(WatchedEvent event) {
		this.logger.warn("onNodeChanged. event={}", event);
		String path = event.getPath();
		String[] temp = path.split("/");
		String name = temp[temp.length-1];
		try {
			if(event.getType() == EventType.NodeCreated){
				this.logger.warn("NodeCreated. path={}", path);
				String uri = this.cacheNode(path, name);
				if(uri!=null){
					this.listener.onAdded(path, uri);
				}
			}else if(event.getType() == EventType.NodeDataChanged){
				this.logger.warn("NodeDataChanged. path={}", path);
				this.cacheNode(path, name);
			}else if(event.getType() == EventType.NodeDeleted){
				this.logger.warn("NodeDeleted. path={}", path);
				String uri = this.nodes.get(name);
				this.nodes.put(name, "DELETED");
				this.listener.onRemoved(path, uri);
			}else if(event.getType() == EventType.NodeChildrenChanged){
				this.logger.warn("NodeChildrenChanged. path={}", path);
				this.cacheNode(path, name);
			}
		} catch (KeeperException e) {
			this.logger.error("onNodeChanged error. path="+path+", event="+event, e);
		} catch (InterruptedException e) {
			this.logger.error("onNodeChanged error. path="+path+", event="+event, e);
		}
	}
	
	public Map<String, String> getNodes(){
		return nodes;
	}

	public AppNodeListener getListener() {
		return listener;
	}

	public void setListener(AppNodeListener listener) {
		this.listener = listener;
	}
}

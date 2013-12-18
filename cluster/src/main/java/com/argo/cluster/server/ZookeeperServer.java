package com.argo.cluster.server;

import com.argo.core.json.GsonUtil;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

public class ZookeeperServer {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ZooKeeper zkServer;
	private ZookeeperConfig config;
	private byte[] empty = "1".getBytes();
	
	public ZookeeperServer(ZookeeperConfig config) {
		this.config = config;
	}
	
	public synchronized void connect(Watcher watcher) throws IOException{
		this.zkServer = new ZooKeeper(this.config.getHost(), this.config.getTTL(), watcher);
	}
	
	public synchronized void reconnect(Watcher watcher) throws IOException{
		this.zkServer = new ZooKeeper(this.config.getHost(), this.config.getTTL(), watcher);
	}
	
	public ZooKeeper getZK(){
		return this.zkServer;
	}
	
	public void createFolderNodes(String path, CreateMode mode) throws KeeperException, InterruptedException{
        if (path.startsWith("/")){
            path = path.substring(1);
        }
		String[] tmp = path.split("/");
		String tpath = "";
		int i = 0;
		while(i<tmp.length){
			tpath = tpath + "/" + tmp[i];
            if (this.logger.isDebugEnabled()){
                this.logger.debug("Create Folder Node, path={}", tpath);
            }
            if (tpath.endsWith("/")){
                break;
            }
			Stat stat = this.zkServer.exists(tpath, false);
			if(stat==null){
				this.zkServer.create(tpath, empty, ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
			}
			i++;
		}
	}
	
	public void createDataNode(String path, byte[] data, CreateMode mode) throws KeeperException, InterruptedException{
		Stat stat = this.zkServer.exists(path, false);
		if(stat==null){
			this.zkServer.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
		}else{
			this.zkServer.setData(path, data, -1);
		}
	}
	
	public void createDataNode(String path, Object object, CreateMode mode) throws KeeperException, InterruptedException{
		byte[] data = GsonUtil.toJson(object).getBytes(Charset.forName("UTF-8"));
		this.createDataNode(path, data, mode);
	}
	
	public Object getNodeData(String path, Watcher watcher) throws KeeperException, InterruptedException{
		byte[] data = this.zkServer.getData(path, watcher, null);
		return GsonUtil.asT(Object.class, new String(data));
	}
	
	public boolean watch(String path, Watcher watcher){
		try {
			this.zkServer.exists(path, watcher);
			return true;
		} catch (KeeperException e) {
			this.logger.info("ZKServerClient watch error. path="+path, e);
		} catch (InterruptedException e) {
			this.logger.info("ZKServerClient watch errro. path="+path, e);
		}
		return false;
	}
	
	public synchronized void shutdown(){
		if(this.zkServer == null){
			return;
		}
		try {
			this.zkServer.close();
			this.zkServer = null;
			this.logger.info("Zookeeper ServerClient Shutdown.");
		} catch (InterruptedException e) {
			
		}
	}
}

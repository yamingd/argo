package com.argo.elasticsearch;

import com.argo.core.configuration.AbstractConfig;

import java.util.List;
import java.util.Map;

public class ElasticSearchConfig extends AbstractConfig {
	
	private static final String ROOT_PATH = "elasticsearch";
	
	/**
	 * 用来决定读取esclient-$cluster.yaml配置文件.
	 */
	private String cluster;
    private String confName = "";
	
	@Override
	public void afterPropertiesSet() throws Exception {
        confName = "esclient-"+cluster;
		this.cfgFile = confName + ".yaml";
		super.afterPropertiesSet();
	}
	
	public String getClassPathRoot(){
		return ROOT_PATH;
	}
	
	/**
	 * Read transport configuration
	 * @return
	 */
	public Map getTransport(){
		Map ret = this.get(Map.class, "transport");
		return ret;
	}
	
	/**
	 * Read factory bean configuration
	 * @return
	 */
	public Map getPolicy(){
		Map ret = this.get(Map.class, "policy");
		return ret;
	}
	
	/**
	 * Read Nodes.
	 * @return
	 */
	public List getNodesConfig(){
		List ret = this.get(List.class, "nodes");
		return ret;
	}
	
	/**
	 * Read Index Alias
	 * @return
	 */
	public Map getAlias(){
		Map ret = this.get(Map.class, "alias");
		return ret;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

    @Override
    public String getConfName() {
        return confName;
    }
}

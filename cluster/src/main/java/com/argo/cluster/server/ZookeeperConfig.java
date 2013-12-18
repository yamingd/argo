package com.argo.cluster.server;

import com.argo.core.configuration.AbstractConfig;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;

public class ZookeeperConfig extends AbstractConfig {
	
	private Integer defaultTTL = 30000;
    private Map app = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.cfgFile = "zookeeper.yaml";
        super.afterPropertiesSet();
        ZookeeperConfig.instance = this;
        this.app = this.get(Map.class, "app");
    }

	public String getAppName(){
		return ObjectUtils.toString(this.app.get("name"));
	}
	
	public String getAppURI(){
        return ObjectUtils.toString(this.app.get("uri"));
	}
	
	public String getHost(){
		return this.get(String.class, "hosts");
	}
	
	public Integer getTTL(){
        Integer timeout = this.get(Integer.class, "timeout");
        if (timeout != null){
            return timeout * 1000;
        }
		return defaultTTL;
	}
	
	public String getRootPath(){
		return this.get(String.class, "root");
	}
	
	public static ZookeeperConfig instance;

    @Override
    public String getConfName() {
        return "zookeeper";
    }
}

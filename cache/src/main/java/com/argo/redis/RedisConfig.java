package com.argo.redis;

import com.argo.core.configuration.AbstractConfig;

public class RedisConfig extends AbstractConfig {

    private static final String confName = "redis";

    public static RedisConfig instance = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (instance != null){
           return;
        }
        cfgFile = confName + ".yaml";
        super.afterPropertiesSet();
        RedisConfig.instance = this;
    }

	public Integer getMaxActive(){
		return super.get(Integer.class, "maxActive", 100);
	}
	
	public Integer getMaxIdle(){
        return super.get(Integer.class, "maxIdle", 100);
	}
	
	public Integer getTimeoutWait(){
        return super.get(Integer.class, "timeout", 5000);
	}
	
	public String getServiceIp(){
        return super.get(String.class, "host");
	}
	
	public Integer getServicePort(){
        return super.get(Integer.class, "port", 6379);
	}

    public boolean getTestOnBorrow(){
        return super.get(Boolean.class, "testOnBorrow", true);
    }

    public boolean getTestWhileIdle(){
        return super.get(Boolean.class, "testWhileIdle", true);
    }

    @Override
    public String getConfName() {
        return confName;
    }
}

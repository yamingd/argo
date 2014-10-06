package com.argo.service;

import com.argo.core.configuration.AbstractConfig;
import com.argo.core.configuration.ConfigListener;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 */
public class ServiceConfig extends AbstractConfig implements ConfigListener {

    public static ServiceConfig instance;
    private static final String confName = "service";
    private List<String> services;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.cfgFile = confName + ".yaml";
        super.afterPropertiesSet();
        ServiceConfig.instance = this;
        services = this.get(List.class, "rmis");
    }

    public Map getMail(){
        Map ret = this.get(Map.class, "mail");
        if (ret == null){
            this.logger.error("Can't not find mail config in service.yaml");
        }
        return ret;
    }

    public Map getRPC(){
        Map ret = this.get(Map.class, "rpc");
        if (ret == null){
            this.logger.error("Can't not find rpc config in service.yaml");
        }
        return ret;
    }

    public List<String> getServiceServers(String name){
        List<String> addrs = this.get(List.class, name);
        return addrs;
    }

    public List<String> getServices() {
        return services;
    }

    public boolean hasService(String name){
        return services.contains(name);
    }

    @Override
    public String getConfName() {
        return confName;
    }

}

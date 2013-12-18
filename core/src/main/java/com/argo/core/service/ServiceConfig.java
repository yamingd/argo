package com.argo.core.service;

import com.argo.core.configuration.AbstractConfig;
import com.argo.core.configuration.ConfigListener;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-6
 * Time: 下午9:26
 */
public class ServiceConfig extends AbstractConfig implements ConfigListener {

    public static ServiceConfig instance;
    private static final String confName = "service";

    @Override
    public void afterPropertiesSet() throws Exception {
        this.cfgFile = confName + ".yaml";
        super.afterPropertiesSet();
        ServiceConfig.instance = this;
    }

    public Map getMail(){
        Map ret = this.get(Map.class, "mail");
        if (ret == null){
            this.getLogger().error("Can't not find mail config in service.yaml");
        }
        return ret;
    }

    public Map getTasks(){
        Map ret = this.get(Map.class, "tasks");
        if (ret == null){
            this.getLogger().error("Can't not find mail config in service.yaml");
        }
        return ret;
    }

    public Map getBeans(){
        Map ret = this.get(Map.class, "beans");
        if (ret == null){
            this.getLogger().error("Can't not find beans config in service.yaml");
        }
        return ret;
    }

    public Map getRPC(){
        Map ret = this.get(Map.class, "rpc");
        if (ret == null){
            this.getLogger().error("Can't not find rpc config in service.yaml");
        }
        return ret;
    }

    public String getService(String name){
        Map ret = this.get(Map.class, "beans");
        if (ret == null){
            this.getLogger().error("Can't not find beans config in service.yaml");
            return null;
        }
        return ObjectUtils.toString(ret.get(name));
    }

    @Override
    public String getConfName() {
        return confName;
    }

}

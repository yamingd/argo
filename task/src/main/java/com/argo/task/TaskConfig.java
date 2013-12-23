package com.argo.task;

import com.argo.core.configuration.AbstractConfig;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-20
 * Time: 下午9:36
 */
@Component
public class TaskConfig extends AbstractConfig {

    private static final String confName = "task";

    public static TaskConfig instance = null;

    private boolean enabled;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.cfgFile = confName+".yaml";
        super.afterPropertiesSet();
        TaskConfig.instance = this;
        this.enabled = this.get(Boolean.class, "enabled", false);
    }

    public boolean isEnabled(){
        return enabled;
    }

    public Map getTask(String name){
        return this.get(Map.class, name);
    }

    @Override
    public String getConfName() {
        return confName;
    }
}

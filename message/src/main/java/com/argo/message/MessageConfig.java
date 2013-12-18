package com.argo.message;

import com.argo.core.configuration.AbstractConfig;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-1
 * Time: 上午8:52
 */
public class MessageConfig extends AbstractConfig {

    public static MessageConfig current = null;
    private static final String confName = "message";

    private Map queues = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (MessageConfig.current != null){
            return;
        }
        this.cfgFile = confName + ".yaml";
        MessageConfig.current = this;
        super.afterPropertiesSet();
    }

    public String getNS(){
        return ObjectUtils.toString(this.cfg.get("ns"));
    }

    public String getProvider(){
        return ObjectUtils.toString(this.cfg.get("provider"));
    }

    public Map getQueues(){
        if (this.queues != null){
            return this.queues;
        }
        this.queues = this.get(Map.class, "queue");
        if (this.queues == null){
            this.queues = new HashMap();
        }
        return this.queues;
    }

    @Override
    public String getConfName() {
        return confName;
    }
}

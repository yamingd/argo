package com.argo.db;

import com.argo.db.template.ServiceBase;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yaming on 2014/12/8.
 */
@Component
public class ServiceInstanceFactory implements InitializingBean {

    public static ServiceInstanceFactory context;

    private Map<String, ServiceBase> maps = new HashMap<String, ServiceBase>();

    public void add(Class<?> entity, ServiceBase serviceBase){
        maps.put(entity.getName(), serviceBase);
    }

    public ServiceBase get(Class<?> entity){
        return maps.get(entity.getName());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        context = this;
    }
}

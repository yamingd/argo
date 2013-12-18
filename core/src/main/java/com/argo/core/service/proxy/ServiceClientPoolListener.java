package com.argo.core.service.proxy;

import com.argo.core.service.ServicePoolListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-15
 * Time: 下午1:12
 */
public class ServiceClientPoolListener implements ServicePoolListener, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConcurrentHashMap<String, List<String>> servicesMap = new ConcurrentHashMap<String, List<String>>();
    private Random rand = new Random();


    @Override
    public void onServiceChanged(String name, List<String> urls) {
        this.servicesMap.put(name, urls);
    }

    @Override
    public String select(String serviceName){
        List<String> servers = this.servicesMap.get(serviceName);
        if(servers != null && servers.size()>0){
            int index = rand.nextInt(servers.size());
            return servers.get(index);
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServiceClientPoolListener.instance = this;
    }

    public  static ServiceClientPoolListener instance = null;
}

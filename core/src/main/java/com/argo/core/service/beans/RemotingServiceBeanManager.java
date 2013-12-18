package com.argo.core.service.beans;

import com.argo.core.service.ServicePublishListener;
import com.argo.core.service.ServiceConfig;
import com.argo.core.service.factory.ServiceLocator;
import com.argo.core.utils.IpUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * 远程服务管理者.
 *
 * @author yaming_deng
 * @date 2013-1-21
 */
public class RemotingServiceBeanManager {

    public static final Logger logger = LoggerFactory.getLogger(RemotingServiceBeanManager.class);

	private static Map<String, String> beans = new HashMap<String, String>();
	
	/**
	 * 添加远程服务.
	 * @param beanName
	 * @param serviceName
	 */
	public static void add(String beanName, String serviceName){
		beans.put(beanName, serviceName);
	}

    public static void remove(String serviceName){
        ServicePublishListener sp = ServiceLocator.instance.get("servicePublisher");
        if (sp == null){
            return;
        }
        sp.remove(serviceName);
    }

    public static void remove(String serviceName, String uri){
        ServicePublishListener sp = ServiceLocator.instance.get("servicePublisher");
        if (sp == null){
            return;
        }
        sp.remove(serviceName, uri);
    }
	/**
	 * 
	 * @return
	 */
	public static Map<String, String> getAll(){
		return beans;
	}

    public static void publishService(){
        if (ServiceLocator.instance == null){
            return;
        }
        ServicePublishListener sp = ServiceLocator.instance.get("servicePublisher");
        if (sp == null){
            logger.error("servicePublisher is NULL.");
            return;
        }
        String[] ipAddress = IpUtil.getHostServerIp();
        String rpcPort = ObjectUtils.toString(ServiceConfig.instance.getRPC().get("port"));
        String surl = ipAddress[0] + ":" + rpcPort;
        if (logger.isDebugEnabled()){
            logger.debug("Add Service Endpoint at: " + surl);
        }
        Map<String, String> services = RemotingServiceBeanManager.getAll();
        Iterator<String> itor = services.keySet().iterator();
        while(itor.hasNext()){
            String beanName = itor.next();
            String serviceName = services.get(beanName);
            sp.publish(serviceName, surl);
        }
    }
}

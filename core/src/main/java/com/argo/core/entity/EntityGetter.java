package com.argo.core.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 */
public class EntityGetter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * com.sample.Post ==> postService
     * @param clazz
     * @return
     */
    public static String getServiceName(Class<?> clazz){
        String name = clazz.getSimpleName();
        String sname = name.substring(0, 1).toLowerCase() + name.substring(1);
        sname = sname + "Service";
        return name;
    }

    private Long oid = null;

    public EntityGetter(Long oid){
        this.oid = oid;
    }

    public EntityGetter setOid(Long oid){
        this.oid = oid;
        return this;
    }

    public <T> T get(Class<T> clazz){
        /*T o = null;

        if (service == null){
            String serviceName = getServiceName(clazz);
            try {
                service = ServiceLocator.instance.get(BaseService.class, serviceName);
            } catch (Exception e) {
                logger.error("EntityGetter Can't find Service. name="+serviceName, e);
            }
        }
        try {
            o = service.findById(clazz, this.oid) ;
        } catch (ServiceException e) {
            logger.error("EntityGetter Can't find Entity. oid="+this.oid+", clazz="+clazz, e);
        }

        return o;*/
        return null;
    }

}

package com.argo.core.entity;

import com.argo.core.base.BaseService;
import com.argo.core.exception.ServiceException;
import com.argo.core.service.factory.ServiceLocator;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 */
public class EntityGetter {

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

    public <T> T get(Class<T> clazz) throws ServiceException {
        T o;

        String serviceName = getServiceName(clazz);

        BaseService service = ServiceLocator.instance.get(BaseService.class, serviceName);

        o = service.findById(clazz, this.oid) ;

        return o;
    }
}

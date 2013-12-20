package com.argo.couchbase;

import com.argo.core.exception.ServiceException;
import com.argo.core.service.factory.ServiceLocator;
import com.argo.couchbase.exception.BucketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;


/**
 * Created by yamingd on 13-12-19.
 */
public class BucketBaseServiceImpl implements BucketBaseService, InitializingBean {

    protected CouchbaseTemplate cbTemplate;

    @Autowired
    protected ServiceLocator serviceLocator;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public CouchbaseTemplate getTemplate() {
        return cbTemplate;
    }

    @Override
    public <T extends BucketEntity> boolean add(T entity) throws ServiceException {
        try {
            Long oid = this.cbTemplate.uuid(entity.getClass());
            entity.setOid(oid);
            entity.setCreateAt(new Date());
            this.cbTemplate.save(entity);
            return true;
        } catch (BucketException e) {
            throw new ServiceException("add Error.", e);
        }
    }

    @Override
    public <T extends BucketEntity> boolean update(T entity) throws ServiceException {
        try {
            entity.setUpdateAt(new Date());
            this.cbTemplate.update(entity);
            return true;
        } catch (BucketException e) {
            throw new ServiceException("update Error.", e);
        }
    }

    @Override
    public <T extends BucketEntity> boolean remove(Class<T> clazz, Long oid) throws ServiceException {
        try {
            T entity = this.cbTemplate.findById(clazz, oid);
            this.cbTemplate.remove(entity);
            return true;
        } catch (BucketException e) {
            throw new ServiceException("remove Error. oid="+oid+",clazz="+clazz, e);
        }
    }

    @Override
    public <T> T findById(Class<T> clazz, Long oid) throws ServiceException{
        T o = null;
        try {
            o = this.cbTemplate.findById(clazz, oid);
        } catch (BucketException e) {
            throw new ServiceException("findById Error. oid="+oid+",clazz="+clazz, e);
        }
        return o;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String name = this.serviceLocator.getServiceName(CouchbaseTemplate.class);
        cbTemplate = this.serviceLocator.get(name);
    }
}

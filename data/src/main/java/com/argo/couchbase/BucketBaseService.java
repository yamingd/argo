package com.argo.couchbase;

import com.argo.core.base.BaseService;
import com.argo.core.exception.ServiceException;

/**
 * Created by yamingd on 13-12-19.
 */
public interface BucketBaseService extends BaseService {

    CouchbaseTemplate getTemplate();

    <T extends BucketEntity> boolean add(T entity) throws ServiceException;
    <T extends BucketEntity> boolean update(T entity) throws ServiceException;
    <T extends BucketEntity> boolean remove(Class<T> clazz, Long oid) throws ServiceException;

}

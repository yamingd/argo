package com.argo.core.base;

import com.argo.core.exception.ServiceException;

public interface BaseService {

    <T> T findById(Class<T> clazz, Long oid) throws ServiceException;

}

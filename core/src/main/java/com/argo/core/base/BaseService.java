package com.argo.core.base;

import com.argo.core.exception.EntityNotFoundException;

public interface BaseService<T> {

    T findById(Long oid) throws EntityNotFoundException;

}

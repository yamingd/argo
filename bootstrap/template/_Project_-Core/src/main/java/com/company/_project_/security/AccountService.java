package com.company._project_.security;

import com.argo.core.exception.EntityNotFoundException;

/**
 * Created by Yaming on 2014/10/20.
 */
public interface AccountService {

    Account findById(long id) throws EntityNotFoundException;

    Account findByUserName(String userName) throws EntityNotFoundException;
}

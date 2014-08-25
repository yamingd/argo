package com.company.user.service;

import com.argo.core.exception.EntityNotFoundException;
import com.company.user.User;

import java.util.Map;

/**
 * Created by yaming_deng on 14-8-19.
 */
public interface UserService {

    void addUser(User user) throws Exception;
    void updateUser(Long uid, Map<String, Object> args) throws Exception;

    void confirm(Long uid) throws Exception;

    User findById(Long uid)throws EntityNotFoundException;

    User findByEmail(String email) throws EntityNotFoundException;

    User findByUserName(String userName) throws EntityNotFoundException;

}

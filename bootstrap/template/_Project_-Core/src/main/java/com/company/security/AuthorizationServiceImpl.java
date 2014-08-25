package com.company.security;

import com.argo.core.base.BaseBean;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.PermissionDeniedException;
import com.argo.core.exception.UserNotAuthorizationException;
import com.argo.core.password.PasswordServiceFactory;
import com.argo.core.security.AuthorizationService;
import com.argo.core.web.session.SecurityIdGenerator;
import com.company.user.User;
import com.company.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

/**
 * Created by yaming_deng on 14-8-20.
 */
@Component
public class AuthorizationServiceImpl extends BaseBean implements AuthorizationService<User> {

    @Autowired
    private PasswordServiceFactory passwordServiceFactory;

    @Override
    public User verifyCookie(String value) throws UserNotAuthorizationException {
        BigInteger uid = SecurityIdGenerator.Id62.decode(value);
        UserService userService = this.serviceLocator.get(UserService.class);
        try {
            User user = userService.findById(uid.longValue());
            return user;
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw new UserNotAuthorizationException("User not found. uid=" + uid);
        }
    }

    @Override
    public User verifyUserPassword(String userName, String password) {
        UserService userService = this.serviceLocator.get(UserService.class);
        try {
            User user = userService.findByUserName(userName);
            boolean flag = passwordServiceFactory.getDefaultService().validate(password, user.getEmail(), user);
            if (flag){
                return user;
            }
            return null;
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void verifyAccess(String url) throws PermissionDeniedException {

    }
}

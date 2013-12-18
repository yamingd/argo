package com.argo.core.security;

import com.argo.core.base.BaseUser;
import com.argo.core.exception.PermissionDeniedException;
import com.argo.core.exception.UserNotAuthorizationException;

/**
 * 授权和鉴权认证服务.
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-11-17
 * Time: 上午10:02
 */
public interface AuthorizationService {

    /**
     * 验证Cookie的UserId
     * @param uid
     * @return
     * @throws UserNotAuthorizationException
     */
    BaseUser verifyCookie(String uid) throws UserNotAuthorizationException;

    /**
     * 验证用户登录
     * @param userName
     * @param password
     * @return
     */
    boolean verifyUserPassword(String userName, String password);

    /**
     * 验证用户是否有权访问
     * @param url
     * @return
     */
    void verifyAccess(String url) throws PermissionDeniedException;
}

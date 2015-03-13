package com.argo.core.security;

import com.argo.core.base.BaseUser;
import com.argo.core.exception.PermissionDeniedException;
import com.argo.core.exception.UserNotAuthorizationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 授权和鉴权认证服务.
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-11-17
 * Time: 上午10:02
 */
public interface AuthorizationService<T extends BaseUser> {

    /**
     * 验证Cookie的UserId
     * @param uid
     * @return
     * @throws UserNotAuthorizationException
     */
    T verifyCookie(HttpServletRequest request, HttpServletResponse response,String uid) throws UserNotAuthorizationException;

    /**
     * 验证用户登录
     * @param userName
     * @param password
     * @return
     */
    T verifyUserPassword(String userName, String password);

    /**
     * 验证用户
     * @param user
     * @param password
     * @return
     */
    T verifyUserPassword(T user, String password);
    /**
     * 验证用户是否有权访问
     * @param url
     * @return
     */
    void verifyAccess(String url) throws PermissionDeniedException;
}

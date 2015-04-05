package com.argo.web.controllers;

import com.argo.acl.SysMenu;
import com.argo.acl.SysUser;
import com.argo.acl.service.SysMenuService;
import com.argo.acl.service.SysRoleUserService;
import com.argo.acl.service.SysUserService;
import com.argo.core.base.BaseUser;
import com.argo.core.exception.PermissionDeniedException;
import com.argo.core.exception.UserNotAuthorizationException;
import com.argo.core.web.MvcController;
import com.argo.core.web.WebContext;
import com.argo.core.web.session.SessionCookieHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 4/6/15.
 */
public abstract class AdminBaseController extends MvcController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleUserService sysRoleUserService;

    @Autowired
    private SysMenuService sysMenuService;

    public boolean needLogin() {
        return true;
    }

    @Override
    public BaseUser verifyCookie(HttpServletRequest request, HttpServletResponse response)
            throws UserNotAuthorizationException {
        try {
            String uid = SessionCookieHolder.getCurrentUID(request, "ba");
            SysUser user = this.sysUserService.findById(Long.parseLong(uid));
            Cookie cookie = SessionCookieHolder.getCookie(request, SessionCookieHolder.KSESS);
            if (cookie == null){
                SysUser user1 = new SysUser();
                user1.setUid(user.getUid());
                user1.setLoginAt(new Date());
                user1.setLoginIp(WebContext.getContext().getRequestIp());
                this.sysUserService.updateUser(user1);
                SessionCookieHolder.setCookie(response, SessionCookieHolder.KSESS, new Date().getTime() + "");
            }
            return user;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UserNotAuthorizationException("User not found. ");
        }
    }

    @Override
    public void verifyAccess(String url) throws PermissionDeniedException {

    }

    @Override
    public void init() throws Exception {
        BaseUser user = this.getCurrentUser();
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
        if (request.getMethod().equalsIgnoreCase("get")){
            if (user != null && !user.isAnonymous()){
                SysUser account = (SysUser)user;
                List<SysMenu> menus = sysMenuService.findByUserId(account.getId());
                request.setAttribute("sysMenus", menus);

                List<Integer> roleIds = sysRoleUserService.findByUser(account.getId());
                request.setAttribute("isSuper", roleIds.contains(1));
            }
        }
    }
}

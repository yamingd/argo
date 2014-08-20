package com.argo.core.web.Interceptor;

import com.argo.core.ContextConfig;
import com.argo.core.base.BaseUser;
import com.argo.core.configuration.SiteConfig;
import com.argo.core.exception.PermissionDeniedException;
import com.argo.core.exception.UserNotAuthorizationException;
import com.argo.core.metric.MetricCollectorImpl;
import com.argo.core.security.AuthorizationService;
import com.argo.core.service.factory.ServiceLocator;
import com.argo.core.utils.IpUtil;
import com.argo.core.web.AnonymousUser;
import com.argo.core.web.CSRFToken;
import com.argo.core.web.MvcController;
import com.argo.core.web.WebContext;
import com.argo.core.web.session.SessionCookieHolder;
import com.argo.core.web.session.SessionUserHolder;
import com.google.common.io.BaseEncoding;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-11-17
 * Time: 上午9:56
 */
public class HandlerPrepareAdapter extends HandlerInterceptorAdapter {

    private final String cookieId = "_after";

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        if (logger.isDebugEnabled()){
            //logger.debug("preHandle incoming request. contextPath="+request.getContextPath());
            //logger.debug("handler is {} ", handler.getClass());
        }

        String url = request.getRequestURI();
        url = url.replace(request.getContextPath(), "");
        if (!url.startsWith("/asserts/")){
            MetricCollectorImpl.current().incrementCounter(handler.getClass(), url);
        }else{
            //读取css, js, image等，直接返回
            return true;
        }

        WebContext.getContext().setRequestIp(IpUtil.getIpAddress(request));
        WebContext.getContext().setRootPath(request.getContextPath());
        ContextConfig.set("contextPath", request.getContextPath());

        Map app = SiteConfig.instance.getApp();

        AuthorizationService authorizationService = null;
        String loginUrl = ObjectUtils.toString(app.get("login"));
        String currentUid = null;
        try {
            currentUid = SessionCookieHolder.getCurrentUID(request);
        } catch (UserNotAuthorizationException e) {
            logger.debug("UserNotAuthorizationException currentUid="+currentUid);
        }
        BaseUser user = null;
        if (StringUtils.isNotBlank(currentUid)){
            // 若是远程服务，则需要配置bean.authorizationService的实现
            // 若是本地服务，不需要配置
            if (logger.isDebugEnabled()){
                logger.debug("preHandle currentUid="+currentUid);
            }
            authorizationService = ServiceLocator.instance.get(AuthorizationService.class);
            if (authorizationService != null){
                try{
                    user = authorizationService.verifyCookie(currentUid);
                    SessionUserHolder.set(user);
                    if (logger.isDebugEnabled()){
                        logger.debug("preHandle verifyCookie is OK. BaseUser=" + user.getUserName());
                    }
                    String lastAccessUrl = this.getLastAccessUrl(request);
                    if (lastAccessUrl != null){
                        response.sendRedirect(lastAccessUrl);
                        return false;
                    }
                }catch (UserNotAuthorizationException ex){
                    saveLastAccessUrl(request, response);
                    response.sendRedirect(loginUrl);
                    return false;
                }
            }
        }
        if (user == null){
            user = new AnonymousUser();
        }
        request.setAttribute("currentUser", user);
        if (request.getMethod().equalsIgnoreCase("GET")){
            request.setAttribute("csrf", new CSRFToken(request, response, user));
        }
        if (logger.isDebugEnabled()){
            logger.debug("preHandle currentUid="+currentUid);
        }
        if (authorizationService == null){
            authorizationService = ServiceLocator.instance.get(AuthorizationService.class);
        }
        if (authorizationService != null){
            try {
                authorizationService.verifyAccess(request.getRequestURI());
                if (logger.isDebugEnabled()){
                    logger.debug("preHandle verifyAccess is OK.");
                }
            } catch (PermissionDeniedException e) {
                logger.warn("You do not have permission to access. " + request.getRequestURI());
                String deniedUrl = (String)app.get("denied");
                response.sendRedirect(deniedUrl);
                return false;
            }
        }

        MvcController c = this.getController(handler);
        if (c != null){
            c.init();
        }

        return true;
    }

    private void saveLastAccessUrl(HttpServletRequest request, HttpServletResponse response){
        String lastAccessUrl = request.getRequestURL() + "?" + request.getQueryString();
        lastAccessUrl = BaseEncoding.base64Url().encode(lastAccessUrl.getBytes());
        SessionCookieHolder.setCookie(response, cookieId, lastAccessUrl, 3600*8);
    }
    private String getLastAccessUrl(HttpServletRequest request){
        Cookie cookie = SessionCookieHolder.getCookie(request, cookieId);
        if (cookie == null){
            return null;
        }
        byte[] lastAccessUrl = BaseEncoding.base64Url().decode(cookie.getValue());
        return new String(lastAccessUrl);
    }

    private MvcController getController(Object handler){
        if(handler instanceof MvcController){
            return (MvcController)handler;
        }else if(handler instanceof HandlerMethod){
            HandlerMethod hm = (HandlerMethod)handler;
            if(hm.getBean() instanceof MvcController){
                return (MvcController)hm.getBean();
            }
        }
        return null;
    }
}

package com.argo.core.web;

import com.argo.core.base.BaseUser;
import com.argo.core.exception.CookieInvalidException;
import com.argo.core.exception.PermissionDeniedException;
import com.argo.core.exception.UserNotAuthorizationException;
import com.argo.core.msgpack.MsgPackResponse;
import com.argo.core.protobuf.ProtobufResponse;
import com.argo.core.security.AuthorizationService;
import com.argo.core.utils.TokenUtil;
import com.argo.core.web.Interceptor.ExceptionGlobalResolver;
import com.argo.core.web.session.SessionCookieHolder;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-15
 * Time: 下午8:12
 */
public abstract class MvcController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected ExceptionGlobalResolver exceptionGlobalResolver = new ExceptionGlobalResolver();

    @Autowired(required=false)
    private AuthorizationService authorizationService;

    /**
     * 获取当前用户
     * @param <T>
     * @return
     * @throws UserNotAuthorizationException
     */
    @ModelAttribute
    public <T extends BaseUser> T getCurrentUser() throws UserNotAuthorizationException {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
//        if (logger.isDebugEnabled()) {
//            logger.debug("getCurrentUser From Request");
//        }
        T o = (T) request.getAttribute("currentUser");
        if (this.needLogin() && (o == null || o.isAnonymous())) {
            throw new UserNotAuthorizationException("401");
        }
        return o;
    }

    /**
     * 检验Cookie
     * @param request
     * @param response
     * @return
     * @throws UserNotAuthorizationException
     */
    public BaseUser verifyCookie(HttpServletRequest request, HttpServletResponse response)
            throws UserNotAuthorizationException, CookieInvalidException {
        String currentUid = null;
        try {
            currentUid = SessionCookieHolder.getCurrentUID(request);
        } catch (UserNotAuthorizationException e) {
            logger.debug("UserNotAuthorizationException currentUid="+currentUid);
        }
        if (null != authorizationService && StringUtils.isNotBlank(currentUid)){
            BaseUser baseUser = this.authorizationService.verifyCookie(request, response, currentUid);
            return baseUser;
        }
        return null;
    }

    /**
     * 检查授权访问
     * @param url
     * @throws PermissionDeniedException
     */
    public void verifyAccess(String url) throws PermissionDeniedException{
        if (!this.needLogin()){
            return;
        }
        if (null != this.authorizationService){
            this.authorizationService.verifyAccess(url);
        }
    }

    public void init() throws Exception {

    }

    public String getMenu(){
        return "default";
    }

    public boolean needLogin() {
        return false;
    }

    /**
     * 记住当前用户
     * @param request
     * @param response
     * @param userId
     */
    public void rememberUser(HttpServletRequest request, HttpServletResponse response, Object userId) {
        String value = String.valueOf(userId);
        String name = SessionCookieHolder.getAuthCookieId();
        try {
            value = TokenUtil.createSignedValue(name, value);
            SessionCookieHolder.setCookie(response, name, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void rememberUser(HttpServletRequest request, HttpServletResponse response, String name, Object userId) {
        String value = String.valueOf(userId);
        try {
            value = TokenUtil.createSignedValue(name, value);
            SessionCookieHolder.setCookie(response, name, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 注销当前用户
     * @param request
     * @param response
     */
    public void clearUser(HttpServletRequest request, HttpServletResponse response) {
        SessionCookieHolder.removeCurrentUID(response);
        request.removeAttribute("currentUser");
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        exceptionGlobalResolver.resolve(request, response, ex);
    }

    protected void wrapError(BindingResult result, JsonResponse actResponse) {
        List<String> fields = Lists.newArrayList();
        for (FieldError error : result.getFieldErrors()) {
            fields.add(error.getDefaultMessage());
        }
        logger.error("Form Error: {}", result);
        actResponse.getData().add(fields);
        actResponse.setCode(6001);
    }

    protected void wrapError(BindingResult result, MsgPackResponse actResponse) throws Exception {
        List<String> fields = Lists.newArrayList();
        for (FieldError error : result.getFieldErrors()) {
            fields.add(error.getDefaultMessage());
        }
        logger.error("Form Error: {}", result);
        actResponse.add(fields);
        actResponse.setCode(6001);
    }

    protected void wrapError(BindingResult result, ProtobufResponse actResponse) throws Exception {
        for (FieldError error : result.getFieldErrors()) {
            actResponse.getBuilder().addErrors(error.getDefaultMessage());
        }
        logger.error("Form Error: {}", result);
        actResponse.getBuilder().setCode(6001);
    }
}

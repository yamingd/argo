package com.argo.core.web;

import com.argo.core.base.BaseUser;
import com.argo.core.exception.UserNotAuthorizationException;
import com.argo.core.service.factory.ServiceLocator;
import com.argo.core.web.session.SessionUserHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-15
 * Time: 下午8:12
 */
public abstract class MvcController {

    @Autowired
    protected ServiceLocator serviceLocator;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public BaseUser getCurrentUser() throws UserNotAuthorizationException {
       if (this.currentUser.isAnonymous()){
           throw new UserNotAuthorizationException("Go login in first.");
       }else{
           return this.currentUser;
       }
    }

    private BaseUser currentUser;
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response){
        this.request = request;
        this.response = response;
        try {
            this.currentUser = SessionUserHolder.get();
        } catch (UserNotAuthorizationException e) {
            this.currentUser = new AnonymousUser();
        }
    }

    public void init() throws Exception {

    }

    public boolean needLogin() {
        return false;
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, HttpServletRequest request) {
        logger.error(request.getPathInfo() + "?" + request.getQueryString());
        logger.error("@@@@Error.", ex);
        return "500";
    }
}

package com.argo.core.web;

import com.argo.core.base.BaseUser;
import com.argo.core.exception.UserNotAuthorizationException;
import com.argo.core.web.session.SessionUserHolder;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-15
 * Time: 下午8:12
 */
public abstract class MvcController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public BaseUser getCurrentUser() throws UserNotAuthorizationException {
       return SessionUserHolder.get();
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

    protected void wrapError(BindingResult result, JsonResponse actResponse){
        List<String> fields = Lists.newArrayList();
        for(FieldError error : result.getFieldErrors()){
            fields.add(error.getDefaultMessage());
        }
        actResponse.getData().add(fields);
    }

    protected void wrapError(BindingResult result, BsonResponse actResponse) throws Exception {
        List<String> fields = Lists.newArrayList();
        for(FieldError error : result.getFieldErrors()){
            fields.add(error.getDefaultMessage());
        }
        actResponse.add(fields);
    }
}

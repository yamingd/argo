package com.argo.core.freemarker;

import com.argo.core.base.BaseUser;
import com.argo.core.exception.UserNotAuthorizationException;
import com.argo.core.web.WebContext;
import com.argo.core.web.session.SessionUserHolder;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-18
 * Time: 下午11:18
 */
public class FTLHelper {

    public String url(String relativeUrl){
        return WebContext.getContext().getContextPath() + relativeUrl;
    }

    public BaseUser currentUser() throws UserNotAuthorizationException {
        BaseUser user = SessionUserHolder.get();
        return user;
    }

}

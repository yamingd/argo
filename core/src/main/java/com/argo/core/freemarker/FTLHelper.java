package com.argo.core.freemarker;

import com.argo.core.ContextConfig;
import com.argo.core.base.BaseUser;
import com.argo.core.exception.UserNotAuthorizationException;
import com.argo.core.web.session.SessionUserHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-18
 * Time: 下午11:18
 */
public class FTLHelper {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public String url(String relativeUrl){
        if (logger.isDebugEnabled()){
            logger.debug("ContextPath:"+ ContextConfig.get("contextPath"));
        }
        String ct = ContextConfig.get("contextPath");
        if("/".equalsIgnoreCase(ct)){
            return relativeUrl;
        }
        return ct + relativeUrl;
    }

    public BaseUser currentUser() throws UserNotAuthorizationException {
        BaseUser user = SessionUserHolder.get();
        return user;
    }

}

package com.argo.core.freemarker;

import com.argo.core.ContextConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.RequestContext;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-18
 * Time: 下午11:18
 */
public class FTLHelper {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public String url(String relativeUrl){
        String ct = ContextConfig.get("contextPath");
        if("/".equalsIgnoreCase(ct)){
            return relativeUrl;
        }
        return ct + relativeUrl;
    }

    public String menuSelected(RequestContext request, String menuUrl, String cssName){
        String url = request.getRequestUri();
        String ct = ContextConfig.get("contextPath");
        url = url.replace(ct, "");
        if (menuUrl.startsWith("/")){
            menuUrl = menuUrl.substring(1);
        }
        if (url.startsWith("/")){
            url = url.substring(1);
        }
        if (menuUrl.length() >0 && url.startsWith(menuUrl)){
            return cssName;
        }
        if (menuUrl.equalsIgnoreCase(url)){
            return cssName;
        }
        return "";
    }
}

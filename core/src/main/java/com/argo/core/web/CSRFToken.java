package com.argo.core.web;

import com.argo.core.base.BaseUser;
import com.argo.core.utils.TokenUtil;
import com.argo.core.web.session.SessionCookieHolder;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-22
 * Time: 下午3:21
 */
public class CSRFToken {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String token = null;
    private BaseUser currentUser = null;

    public CSRFToken(HttpServletRequest request, HttpServletResponse response, BaseUser currentUser) {
        this.request = request;
        this.response = response;
        this.currentUser = currentUser;
        token = ObjectUtils.toString(request.getAttribute("_csrf_"));
        if(StringUtils.isBlank(token)){
            token = request.getParameter("_csrf_");
        }
    }

    public String tag(){
       return getCsrfHtml();
    }

    private String _csrfHtml  = "<input type='hidden' name='_csrf_' value='%s' />";
    /**
     * 同时新开2个Form页面，旧页面的token会失效.
     * @return
     */
    private String getCsrfHtml(){
        String ct = String.format(this._csrfHtml, this.getToken());
        return ct;
    }

    public String getToken(){
        if(StringUtils.isBlank(token)){
            token = TokenUtil.random(currentUser.getPK());
            SessionCookieHolder.setCookie(response, "_csrf_", token);
        }
        return token;
    }
}

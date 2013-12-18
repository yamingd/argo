package com.argo.core.web.Interceptor;

import com.argo.core.configuration.SiteConfig;
import com.argo.core.exception.PermissionDeniedException;
import com.argo.core.exception.UserNotAuthorizationException;
import com.argo.core.json.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-11-17
 * Time: 上午10:44
 */
public class ExceptionHandler implements HandlerExceptionResolver {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Map app = SiteConfig.instance.getApp();
        if (isAjax(request)){
            if (ex instanceof UserNotAuthorizationException){
                String loginUrl = (String)app.get("login");
                Map<String, String> map = new HashMap<String, String>();
                map.put("code", "401");
                map.put("message", ex.getMessage());
                map.put("login", loginUrl);
                GsonUtil.printJsonObject(response, map);
                return null;
            }else if (ex instanceof PermissionDeniedException){
                Map<String, String> map = new HashMap<String, String>();
                map.put("code", "403");
                map.put("message", ex.getMessage());
                GsonUtil.printJsonObject(response, map);
                return null;
            }else{

                String errorString = "Method:"+request.getMethod()+"请求错误:"+request.getRequestURL().toString()+",refererUrl:"+request.getHeader("Referer");
                logger.error(errorString, ex);

                Map<String, String> map = new HashMap<String, String>();
                map.put("code", "500");
                map.put("message", ex.getMessage());
                map.put("refererUrl", request.getHeader("Referer"));
                map.put("page", request.getRequestURL().toString());
                GsonUtil.printJsonObject(response, map);
                return null;
            }
        }else{
            if (ex instanceof UserNotAuthorizationException){
                String loginUrl = (String)app.get("login");
                try {
                    response.sendRedirect(loginUrl);
                    return null;
                } catch (IOException e) {
                    logger.error("Redirect Error", e);
                }
            }else if (ex instanceof PermissionDeniedException){
                String url = (String)app.get("denied");
                try {
                    response.sendRedirect(url);
                    return null;
                } catch (IOException e) {
                    logger.error("Redirect Error", e);
                }
            }else{

                String errorString = "Method:"+request.getMethod()+"请求错误:"+request.getRequestURL().toString()+",refererUrl:"+request.getHeader("Referer");
                logger.error(errorString, ex);

                return new ModelAndView("500");
            }
        }
        return new ModelAndView("home");
    }

    protected boolean isAjax(HttpServletRequest request){
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }
}

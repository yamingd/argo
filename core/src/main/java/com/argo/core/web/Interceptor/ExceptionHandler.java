package com.argo.core.web.Interceptor;

import com.argo.core.configuration.SiteConfig;
import com.argo.core.exception.PermissionDeniedException;
import com.argo.core.exception.UserNotAuthorizationException;
import com.argo.core.json.JsonUtil;
import com.argo.core.protobuf.ProtobufResponse;
import com.argo.core.web.Enums;
import com.argo.core.web.JsonResponse;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        boolean isMobile = request.getHeader(HandlerPrepareAdapter.X_MOBILE) != null;
        if (isAjax(request)){
            return handleJsonRequest(request, response, ex, app);
        }else if(isMobile){
            String contentType = request.getHeader("Accept");
            if (Enums.PROTOBUF_VALUE.equalsIgnoreCase(contentType)) {
                this.handleMobileRequest(request, response, ex, app);
            }else{
                this.handleJsonRequest(request, response, ex, app);
            }
            return null;
        }else{
            return handleNormalRequest(request, response, ex, app);
        }
    }

    private void handleMobileRequest(HttpServletRequest request, HttpServletResponse response, Exception ex, Map app){
        ProtobufResponse pb = new ProtobufResponse();
        if (ex instanceof UserNotAuthorizationException){
            pb.getBuilder().setCode(401).setMsg(ex.getMessage());
            response.setStatus(401);
            writeProtobuf(request, response, pb.build());
        }else if (ex instanceof PermissionDeniedException){
            pb.getBuilder().setCode(403).setMsg(ex.getMessage());
            response.setStatus(403);
            writeProtobuf(request, response, pb.build());
        }else{
            String errorString = "Method:"+request.getMethod()+"请求错误:"+request.getRequestURL().toString()+",refererUrl:"+request.getHeader("Referer");
            logger.error(errorString, ex);
            pb.getBuilder().setCode(500).setMsg(ex.getMessage());
            response.setStatus(500);
            writeProtobuf(request, response, pb.build());
        }
    }

    /**
     * 处理PC请求
     * @param request
     * @param response
     * @param ex
     * @param app
     * @return
     */
    private ModelAndView handleNormalRequest(HttpServletRequest request, HttpServletResponse response, Exception ex, Map app) {
        if (ex instanceof UserNotAuthorizationException){
            String loginUrl = (String)app.get("login");
            try {
                response.setStatus(401);
                response.sendRedirect(loginUrl);
                return null;
            } catch (IOException e) {
                logger.error("Redirect Error", e);
                return new ModelAndView("500");
            }
        }else if (ex instanceof PermissionDeniedException){
            logger.warn("You do not have permission to access. " + request.getRequestURI());
            String url = (String)app.get("denied");
            response.setStatus(403);
            try {
                response.sendRedirect(url);
                return null;
            } catch (IOException e) {
                logger.error("Redirect Error", e);
                return new ModelAndView("500");
            }
        }else{
            String errorString = "Method:"+request.getMethod()+"请求错误:"+request.getRequestURL().toString()+",refererUrl:"+request.getHeader("Referer");
            logger.error(errorString, ex);
            response.setStatus(500);
            return new ModelAndView("500");
        }
    }

    /**
     * 处理JSON请求
     * @param request
     * @param response
     * @param ex
     * @param app
     * @return
     */
    private ModelAndView handleJsonRequest(HttpServletRequest request, HttpServletResponse response, Exception ex, Map app) {
        JsonResponse jsonResponse = new JsonResponse();
        if (ex instanceof UserNotAuthorizationException){
            response.setStatus(401);
            String loginUrl = (String)app.get("login");
            jsonResponse.setCode(401);
            jsonResponse.setMsg(loginUrl);
            writeJson(request, response, jsonResponse);
            return null;
        }else if (ex instanceof PermissionDeniedException){
            response.setStatus(403);
            jsonResponse.setCode(403);
            jsonResponse.setMsg(ex.getMessage());
            writeJson(request, response, jsonResponse);
            return null;
        }else{
            response.setStatus(500);
            String errorString = "Method:"+request.getMethod()+"请求错误:"+request.getRequestURL().toString()+",refererUrl:"+request.getHeader("Referer");
            logger.error(errorString, ex);
            jsonResponse.setCode(500);
            jsonResponse.setMsg(ex.getMessage());
            writeJson(request, response, jsonResponse);
            return null;
        }
    }

    private void writeJson(HttpServletRequest request, HttpServletResponse response, Object o){
        String json = JsonUtil.toJson(o);
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeProtobuf(HttpServletRequest request, HttpServletResponse response, Message o){
        response.setHeader("Content-Type", Enums.PROTOBUF_VALUE);
        try {
            response.getOutputStream().write(o.toByteArray());
            response.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected boolean isAjax(HttpServletRequest request){
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }
}

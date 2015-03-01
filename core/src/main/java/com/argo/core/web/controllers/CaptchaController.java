package com.argo.core.web.controllers;

import com.argo.core.component.CaptchaComponent;
import com.argo.core.web.MvcController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Yaming
 * Date: 2014/12/23
 * Time: 14:30
 */
@Controller
@RequestMapping("/captcha")
public class CaptchaController extends MvcController {

    @Override
    public boolean needLogin() {
        return false;
    }

    @RequestMapping(value = "/{ts}", method= RequestMethod.GET)
    public void get(HttpServletRequest request, HttpServletResponse response,
                    @PathVariable(value = "ts") String ts){
        if (logger.isDebugEnabled()){
            logger.debug("TS:{}, UA: {}", ts, request.getHeader("User-Agent"));
        }
        String token = CaptchaComponent.generateToken(response);
        setResponseHeaders(response);
        try {
            CaptchaComponent.draw(token, response.getOutputStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * Helper method, disables HTTP caching.
     *
     * @param resp
     *            response object to be modified
     */
    protected void setResponseHeaders(HttpServletResponse resp) {
        resp.setContentType("image/" + CaptchaComponent.getFormat());
        resp.setHeader("Cache-Control", "no-cache, no-store");
        resp.setHeader("Pragma", "no-cache");
        long time = System.currentTimeMillis();
        resp.setDateHeader("Last-Modified", time);
        resp.setDateHeader("Date", time);
        resp.setDateHeader("Expires", time);
    }
}

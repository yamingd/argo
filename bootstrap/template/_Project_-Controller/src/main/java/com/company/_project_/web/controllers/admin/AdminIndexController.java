package com.argo.equation.web.controllers.admin;

import com.argo.core.exception.UserNotAuthorizationException;
import com.argo.core.web.JsonResponse;
import com.argo.equation.web.controllers.BaseController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Yaming on 2014/10/13.
 */
@Controller
@RequestMapping("/admin")
public class AdminIndexController extends BaseController {

    @RequestMapping(value = "/", method= RequestMethod.GET)
    public String get(){
        try {
            this.getCurrentUser();
            return "admin/index";
        } catch (UserNotAuthorizationException e) {
            //return "admin/signin";
            return "admin/index";
        }
    }

    /**
     * 登录.
     * 访问URL: /admin/signin
     * @param uname
     * @param passwd
     * @param actResponse
     * @return
     */
    @RequestMapping(value="signin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse signin(
            @RequestParam(value = "uname", defaultValue = "") String uname,
            @RequestParam(value = "passwd", defaultValue = "") String passwd, JsonResponse actResponse){

        return actResponse;
    }
}

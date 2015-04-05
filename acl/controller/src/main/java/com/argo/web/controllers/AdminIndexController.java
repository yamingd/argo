package com.argo.web.controllers;

import com.argo.acl.SysRole;
import com.argo.acl.SysUser;
import com.argo.acl.service.SysRoleService;
import com.argo.acl.service.SysRoleUserService;
import com.argo.acl.service.SysUserService;
import com.argo.core.component.CaptchaComponent;
import com.argo.core.exception.ServiceException;
import com.argo.core.web.JsonResponse;
import com.argo.core.web.session.SessionCookieHolder;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Yaming on 2014/10/13.
 */
@Controller
@RequestMapping("/admin")
public class AdminIndexController extends AdminBaseController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleUserService sysRoleUserService;

    @Autowired
    private SysRoleService sysRoleService;

    public boolean needLogin() {
        return false;
    }

    @RequestMapping(value = "/install", method= RequestMethod.GET)
    public ModelAndView install(ModelAndView view) throws ServiceException {
        SysRole sysRole = new SysRole();
        sysRole.setName("ROLE_SYS_ADMIN");
        sysRole.setName("系统管理员");
        Long roleId = this.sysRoleService.add(sysRole);

        SysUser sysUser = new SysUser();
        sysUser.setName("administrator");
        sysUser.setPasswd("admin@123");
        sysUser.setTitle("系统管理员");

        sysUser = this.sysUserService.addUser(sysUser);
        List<Integer> roleIds = Lists.newArrayList();
        roleIds.add(roleId.intValue());
        this.sysRoleUserService.addBatch(sysUser.getId(), roleIds);

        view.setView(new RedirectView("/admin"));
        return view;
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
            @RequestParam(value = "passwd", defaultValue = "") String passwd,
            @RequestParam(value = "token", defaultValue = "") String token,
            HttpServletRequest request, HttpServletResponse response,
            JsonResponse actResponse){

        //0. 验证码校验
        boolean flag = CaptchaComponent.verifyToken(request, token);
        if (!flag){
            actResponse.setCode(60101);
            actResponse.setMsg("验证码错误");
            return actResponse;
        }

        SysUser user = sysUserService.verifyUserPassword(uname, passwd);

        if (user == null){
            actResponse.setCode(60102);
            actResponse.setMsg("用户名或密码错误");
            return actResponse;
        }

        this.rememberUser(request, response, "ba", user.getUid());

        return actResponse;
    }

    @RequestMapping(value = "signout", method= RequestMethod.GET)
    public ModelAndView signout(ModelAndView view, HttpServletRequest request, HttpServletResponse response){
        SessionCookieHolder.removeCurrentUID(response, "ba");
        request.removeAttribute("currentUser");
        view.setView(new RedirectView("/admin"));
        return view;
    }
}

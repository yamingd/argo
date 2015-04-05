package com.argo.web.controllers.acl;

import com.argo.acl.SysRole;
import com.argo.acl.SysUser;
import com.argo.acl.service.SysRoleService;
import com.argo.acl.service.SysRoleUserService;
import com.argo.acl.service.SysUserService;
import com.argo.core.collections.ConvertFunctions;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.web.JsonResponse;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */

@Controller
@RequestMapping("/a/acl/sys/role/user")
public class SysRoleUserController extends AclBaseController {

    @Autowired
    private SysRoleUserService sysRoleUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysUserService sysUserService;


    @RequestMapping(value="{id}", method = RequestMethod.GET)
    public ModelAndView assign(ModelAndView model, @PathVariable Integer id) throws EntityNotFoundException {

        List<Integer> resIds = sysRoleUserService.findByUser(id);
        List<SysRole> list = sysRoleService.findAll();
        SysUser sysUser = this.sysUserService.findById(id.longValue());

        model.addObject("resIds", resIds);
        model.addObject("resList", list);
        model.addObject("sysUser", sysUser);

        model.setViewName("/admin/acl/sys/role/user-assign");

        return model;
    }

    @RequestMapping(value="save/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postAdd(@PathVariable Integer id, JsonResponse actResponse, HttpServletRequest request) throws Exception {

        String[] temp = request.getParameterValues("roleId");
        List<Integer> roleIds = Lists.transform(Arrays.asList(temp), ConvertFunctions.toIntegerFunction());

        sysRoleUserService.addBatch(id, roleIds);

        return actResponse;
    }
}
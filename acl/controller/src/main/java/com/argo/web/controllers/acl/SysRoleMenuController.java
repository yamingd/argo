package com.argo.web.controllers.acl;

import com.argo.acl.SysMenu;
import com.argo.acl.SysRole;
import com.argo.acl.service.SysMenuService;
import com.argo.acl.service.SysRoleMenuService;
import com.argo.acl.service.SysRoleService;
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
@RequestMapping("/a/acl/sys/role/menu")
public class SysRoleMenuController extends AclBaseController {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysRoleService sysRoleService;

    @RequestMapping(value="{id}", method = RequestMethod.GET)
    public ModelAndView assign(ModelAndView model, @PathVariable Long id) throws EntityNotFoundException {

        List<Integer> resIds = sysRoleMenuService.findByRole(id);
        List<SysMenu> list = sysMenuService.findAll();
        SysRole sysRole = this.sysRoleService.findById(id);

        model.addObject("resIds", resIds);
        model.addObject("resList", list);
        model.addObject("sysRole", sysRole);

        model.setViewName("/admin/acl/sys/role/menu-assign");

        return model;
    }

    @RequestMapping(value="save/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postAdd(@PathVariable Integer id, JsonResponse actResponse, HttpServletRequest request) throws Exception {

        String[] temp = request.getParameterValues("menuId");
        List<Integer> menuIds = Lists.transform(Arrays.asList(temp), ConvertFunctions.toIntegerFunction());

        sysRoleMenuService.addBatch(id, menuIds);

        return actResponse;
    }

}
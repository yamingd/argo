package com.argo.web.controllers.acl;

import com.argo.acl.SysRole;
import com.argo.acl.service.SysRoleService;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.web.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */

@Controller
@RequestMapping("/a/acl/sys/role")
public class SysRoleController extends AclBaseController {

    @Autowired
    private SysRoleService sysRoleService;

    @RequestMapping(value="all", method = RequestMethod.GET)
    public ModelAndView all(ModelAndView model){

        List<SysRole> list = sysRoleService.findAll();
        model.setViewName("acl/sys/role/all");
        model.addObject("roles", list);

        return model;
    }

    @RequestMapping(value="select", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse<SysRole> select(ModelAndView model, JsonResponse<SysRole> actResponse){

        List<SysRole> list = sysRoleService.findAll();
        actResponse.getData().addAll(list);

        return actResponse;
    }

    @RequestMapping(value="add", method = RequestMethod.GET)
    public ModelAndView add(ModelAndView model){

        model.setViewName("acl/sys/role/add");
        model.addObject("role", new SysRole());

        return model;
    }

    @RequestMapping(value="view/{id}", method = RequestMethod.GET)
    public ModelAndView view(ModelAndView model, @PathVariable Long id){

        try {
            SysRole role = sysRoleService.findById(id);
            model.addObject("role", role);
            model.setViewName("acl/sys/role/view");
        } catch (EntityNotFoundException e) {
            RedirectView view = new RedirectView("acl/sys/role/404");
            model.setView(view);
        }

        return model;
    }

    @RequestMapping(value="create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postCreate(@Valid DetailForm form, BindingResult result, JsonResponse actResponse) throws Exception {

        if (result.hasErrors()){
            this.wrapError(result, actResponse);
            return actResponse;
        }

        SysRole role = new SysRole();
        role.setName(form.getName());
        role.setTitle(form.getTitle());
        Long id = sysRoleService.add(role);
        role.setId(id.intValue());

        actResponse.add(role);

        return actResponse;
    }

    @RequestMapping(value="save/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postSave(@Valid DetailForm form, BindingResult result, @PathVariable Long id, JsonResponse actResponse) throws Exception {

        if (result.hasErrors()){
            this.wrapError(result, actResponse);
            return actResponse;
        }

        SysRole role = new SysRole();
        role.setName(form.getName());
        role.setTitle(form.getTitle());
        role.setId(id.intValue());

        sysRoleService.update(role);

        return actResponse;
    }

    @RequestMapping(value="remove/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postRemove(@PathVariable Long id, JsonResponse actResponse) throws Exception {

        if (id != null) {
            sysRoleService.remove(id);
        }

        return actResponse;
    }
}
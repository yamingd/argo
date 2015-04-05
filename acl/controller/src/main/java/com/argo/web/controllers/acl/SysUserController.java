package com.argo.web.controllers.acl;

import com.argo.acl.SysUser;
import com.argo.acl.service.SysUserService;
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
@RequestMapping("/a/acl/sys/user")
public class SysUserController extends AclBaseController {

    @Autowired
    private SysUserService sysUserService;

    @RequestMapping(value="all", method = RequestMethod.GET)
    public ModelAndView all(ModelAndView model){

        List<SysUser> list = sysUserService.findAll();
        model.setViewName("/admin/acl/sys/user/all");
        model.addObject("ds", list);

        return model;
    }

    @RequestMapping(value="select", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse<SysUser> select(ModelAndView model, JsonResponse<SysUser> actResponse){

        List<SysUser> list = sysUserService.findAll();
        actResponse.getData().addAll(list);

        return actResponse;
    }

    @RequestMapping(value="add", method = RequestMethod.GET)
    public ModelAndView add(ModelAndView model){

        model.setViewName("/admin/acl/sys/user/add");
        model.addObject("sysUser", new SysUser());

        return model;
    }

    @RequestMapping(value="view/{id}", method = RequestMethod.GET)
    public ModelAndView view(ModelAndView model, @PathVariable Long id){

        try {
            SysUser role = sysUserService.findById(id);
            model.addObject("sysUser", role);
            model.setViewName("/admin/acl/sys/user/view");
        } catch (EntityNotFoundException e) {
            RedirectView view = new RedirectView("/admin/acl/sys/404");
            model.setView(view);
        }

        return model;
    }

    @RequestMapping(value="passwd/{id}", method = RequestMethod.GET)
    public ModelAndView viewPasswd(ModelAndView model, @PathVariable Long id){

        try {
            SysUser role = sysUserService.findById(id);
            model.addObject("sysUser", role);
            model.setViewName("/admin/acl/sys/user/passwd");
        } catch (EntityNotFoundException e) {
            RedirectView view = new RedirectView("/admin/acl/sys/404");
            model.setView(view);
        }

        return model;
    }

    @RequestMapping(value="create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postCreate(@Valid SysUserForm form, BindingResult result, JsonResponse actResponse) throws Exception {

        if (result.hasErrors()){
            this.wrapError(result, actResponse);
            return actResponse;
        }

        SysUser sysUser = new SysUser();
        sysUser.setName(form.getName());
        sysUser.setTitle(form.getTitle());
        sysUser.setPasswd(form.getPasswd());

        sysUser = sysUserService.addUser(sysUser);

        actResponse.add(sysUser);

        return actResponse;
    }

    @RequestMapping(value="save/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postSave(@Valid SysUserForm form, BindingResult result, @PathVariable Long id, JsonResponse actResponse) throws Exception {

        if (result.hasErrors()){
            this.wrapError(result, actResponse);
            return actResponse;
        }

        SysUser sysUser = new SysUser();
        sysUser.setName(form.getName());
        sysUser.setTitle(form.getTitle());
        sysUser.setId(id.intValue());

        sysUserService.updateUser(sysUser);

        return actResponse;
    }

    @RequestMapping(value="passwd/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postSavePassword(@Valid SysUserForm form, BindingResult result,
                                         @PathVariable Long id, JsonResponse actResponse) throws Exception {

        if (result.hasErrors()){
            this.wrapError(result, actResponse);
            return actResponse;
        }

        SysUser sysUser = new SysUser();
        sysUser.setName(form.getName());
        sysUser.setTitle(form.getTitle());
        sysUser.setId(id.intValue());
        sysUser.setPasswd(form.getPasswd());

        sysUserService.updatePassword(sysUser, null);

        return actResponse;
    }

    @RequestMapping(value="remove/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postRemove(@PathVariable Long id, JsonResponse actResponse) throws Exception {

        if (id != null) {
            sysUserService.remove(id);
        }

        return actResponse;
    }
}
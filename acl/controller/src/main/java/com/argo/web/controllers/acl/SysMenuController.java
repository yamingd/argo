package com.argo.web.controllers.acl;

import com.argo.acl.SysMenu;
import com.argo.acl.service.SysMenuService;
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
@RequestMapping("/a/acl/sys/menu")
public class SysMenuController extends AclBaseController {

    @Autowired
    private SysMenuService sysMenuService;

    @RequestMapping(value="all", method = RequestMethod.GET)
    public ModelAndView all(ModelAndView model){

        List<SysMenu> list = sysMenuService.findAll();
        model.setViewName("/admin/acl/sys/menu/all");
        model.addObject("ds", list);

        return model;
    }

    @RequestMapping(value="select", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse<SysMenu> select(ModelAndView model, JsonResponse<SysMenu> actResponse){

        List<SysMenu> list = sysMenuService.findAll();
        actResponse.getData().addAll(list);

        return actResponse;
    }

    @RequestMapping(value="add", method = RequestMethod.GET)
    public ModelAndView add(ModelAndView model){

        model.setViewName("/admin/acl/sys/menu/add");
        SysMenu item = new SysMenu();
        item.setOrderNo(100);
        model.addObject("item", item);

        return model;
    }

    @RequestMapping(value="view/{id}", method = RequestMethod.GET)
    public ModelAndView view(ModelAndView model, @PathVariable Long id){

        try {
            SysMenu role = sysMenuService.findById(id);
            model.addObject("item", role);
            model.setViewName("/admin/acl/sys/menu/view");
        } catch (EntityNotFoundException e) {
            RedirectView view = new RedirectView("/admin/acl/sys/404");
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

        SysMenu role = new SysMenu();

        role.setPageUrl(form.getUrl());
        role.setTitle(form.getTitle());
        role.setParentId(form.getParentId());
        role.setOrderNo(form.getOrderNo());

        Long id = sysMenuService.add(role);
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

        SysMenu role = new SysMenu();

        role.setPageUrl(form.getUrl());
        role.setTitle(form.getTitle());
        role.setParentId(form.getParentId());
        role.setOrderNo(form.getOrderNo());

        role.setId(id.intValue());

        sysMenuService.update(role);

        return actResponse;
    }

    @RequestMapping(value="remove/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postRemove(@PathVariable Long id, JsonResponse actResponse) throws Exception {

        if (id != null) {
            sysMenuService.remove(id);
        }

        return actResponse;
    }
}
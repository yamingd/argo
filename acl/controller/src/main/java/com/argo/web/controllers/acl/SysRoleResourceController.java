package com.argo.web.controllers.acl;

import com.argo.acl.SysResource;
import com.argo.acl.SysRoleResource;
import com.argo.acl.service.SysResourceService;
import com.argo.acl.service.SysRoleResourceService;
import com.argo.core.web.JsonResponse;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */

@Controller
@RequestMapping("/admin/acl/sys/role/resource")
public class SysRoleResourceController extends AclBaseController {

    @Autowired
    private SysRoleResourceService sysRoleResourceService;

    @Autowired
    private SysResourceService sysResourceService;

    @RequestMapping(value="{id}", method = RequestMethod.GET)
    public ModelAndView assign(ModelAndView model, @PathVariable Long id){

        List<Integer> resIds = sysRoleResourceService.findByRole(id);
        List<SysResource> list = sysResourceService.findAll();

        model.addObject("resIds", resIds);
        model.addObject("resList", list);
        model.setViewName("acl/sys/role/resource-assign");

        return model;
    }

    @RequestMapping(value="add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postAdd(@Valid AssignForm form, BindingResult result, JsonResponse actResponse) throws Exception {

        if (result.hasErrors()){
            List<String> fields = Lists.newArrayList();
            for(FieldError error : result.getFieldErrors()){
                fields.add(error.getField());
            }
            actResponse.setCode(ErrorCodes.FORM_DATA_INVALID);
            actResponse.getData().add(fields);
            return actResponse;
        }

        SysRoleResource item = new SysRoleResource();
        item.setCreateAt(new Date());
        item.setResourceId(form.getItemId());
        item.setRoleId(form.getRoleId());

        sysRoleResourceService.add(item);

        return actResponse;
    }

    @RequestMapping(value="remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postRemove(@Valid AssignForm form, BindingResult result, JsonResponse actResponse) throws Exception {

        if (result.hasErrors()){
            this.wrapError(result, actResponse);
            return actResponse;
        }

        SysRoleResource item = new SysRoleResource();
        item.setResourceId(form.getItemId());
        item.setRoleId(form.getRoleId());

        sysRoleResourceService.remove(item);

        return actResponse;
    }
}
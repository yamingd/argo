package com.argo.web.controllers.acl;

import com.argo.acl.SysRoleUser;
import com.argo.acl.service.SysRoleUserService;
import com.argo.core.web.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Date;

/**
 * Created by $User on 2014-10-08 09:58.
 */

@Controller
@RequestMapping("/a/acl/sys/role/user")
public class SysRoleUserController extends AclBaseController {

    @Autowired
    private SysRoleUserService sysRoleUserService;

    @RequestMapping(value="add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postAdd(@Valid AssignForm form, BindingResult result, JsonResponse actResponse) throws Exception {

        if (result.hasErrors()){
            this.wrapError(result, actResponse);
            return actResponse;
        }

        SysRoleUser item = new SysRoleUser();
        item.setCreateAt(new Date());
        item.setUserId(form.getItemId().longValue());
        item.setRoleId(form.getRoleId());

        sysRoleUserService.add(item);

        return actResponse;
    }

    @RequestMapping(value="remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postRemove(@Valid AssignForm form, BindingResult result, JsonResponse actResponse) throws Exception {

        if (result.hasErrors()){
            this.wrapError(result, actResponse);
            return actResponse;
        }

        SysRoleUser item = new SysRoleUser();
        item.setUserId(form.getItemId().longValue());
        item.setRoleId(form.getRoleId());

        sysRoleUserService.remove(item);

        return actResponse;
    }
}
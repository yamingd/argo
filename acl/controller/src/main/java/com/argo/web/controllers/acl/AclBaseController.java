package com.argo.web.controllers.acl;

import com.argo.core.web.JsonResponse;
import com.argo.core.web.MvcController;
import com.google.common.collect.Lists;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * Created by yaming_deng on 14-8-25.
 */
public abstract class AclBaseController extends MvcController {

    protected void wrapError(BindingResult result, JsonResponse actResponse){
        List<String> fields = Lists.newArrayList();
        for(FieldError error : result.getFieldErrors()){
            fields.add(error.getDefaultMessage());
        }
        actResponse.setCode(ErrorCodes.FORM_DATA_INVALID);
        actResponse.getData().add(fields);
    }

}

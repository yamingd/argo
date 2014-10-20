package com._company_._project_.web.controllers.admin;

import com.argo.core.web.JsonResponse;
import com.argo.core.web.MvcController;
import com.company._project_.ErrorCodes;
import com.google.common.collect.Lists;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * Created by Yaming on 2014/10/13.
 */
public abstract class AdminBaseController extends MvcController {

    public boolean needLogin() {
        return true;
    }

    protected void wrapError(BindingResult result, JsonResponse actResponse){
        List<String> fields = Lists.newArrayList();
        for(FieldError error : result.getFieldErrors()){
            fields.add(error.getDefaultMessage());
        }
        actResponse.setCode(ErrorCodes.FORM_DATA_INVALID);
        actResponse.getData().add(fields);
    }

}

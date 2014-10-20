package com._company_._project_.web.controllers.mobile;

import com.argo.core.web.BsonResponse;
import com.argo.core.web.MvcController;
import com.company._project_.ErrorCodes;
import com.google.common.collect.Lists;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * Created by Yaming on 2014/10/20.
 */
public abstract class MobileBaseController extends MvcController {

    public boolean needLogin() {
        return true;
    }

    protected void wrapError(BindingResult result, BsonResponse actResponse) throws Exception {
        List<String> fields = Lists.newArrayList();
        for(FieldError error : result.getFieldErrors()){
            fields.add(error.getDefaultMessage());
        }
        actResponse.setCode(ErrorCodes.FORM_DATA_INVALID);
        actResponse.add(fields);
    }
}

package com._company_._project_.web.controllers.mobile;

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

}

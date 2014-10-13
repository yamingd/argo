package com.argo.equation.web.controllers.admin;

import com.argo.equation.web.controllers.BaseController;

/**
 * Created by Yaming on 2014/10/13.
 */
public abstract class AdminBaseController extends BaseController {

    public boolean needLogin() {
        return true;
    }

}

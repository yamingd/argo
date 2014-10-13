package com.company._project_.web.controllers.admin;

import com.company._project_.web.controllers.BaseController;

/**
 * Created by Yaming on 2014/10/13.
 */
public abstract class AdminBaseController extends BaseController {

    public boolean needLogin() {
        return true;
    }

}

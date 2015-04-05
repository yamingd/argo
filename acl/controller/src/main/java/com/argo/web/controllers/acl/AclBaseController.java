package com.argo.web.controllers.acl;

import com.argo.web.controllers.AdminBaseController;

/**
 * Created by yaming_deng on 14-8-25.
 */
public abstract class AclBaseController extends AdminBaseController {

    public static final String MENU_SYSTEM = "system";

    @Override
    public String getMenu() {
        return MENU_SYSTEM;
    }

    @Override
    public boolean needLogin() {
        return true;
    }
}

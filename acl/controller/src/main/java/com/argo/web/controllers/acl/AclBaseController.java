package com.argo.web.controllers.acl;

import com.argo.core.web.MvcController;

/**
 * Created by yaming_deng on 14-8-25.
 */
public abstract class AclBaseController extends MvcController {

    public static final String MENU_SYSTEM = "system";

    @Override
    public String getMenu() {
        return MENU_SYSTEM;
    }
}

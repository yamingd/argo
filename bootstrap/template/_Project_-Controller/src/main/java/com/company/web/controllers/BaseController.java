package com.company.web.controllers;

import com.argo.core.web.ActResponse;
import com.argo.core.web.MvcController;
import org.springframework.context.annotation.Scope;

/**
 * Created by yaming_deng on 14-8-25.
 */
@Scope("prototype")
public class BaseController extends MvcController {

    protected ActResponse okResp = new ActResponse();

}

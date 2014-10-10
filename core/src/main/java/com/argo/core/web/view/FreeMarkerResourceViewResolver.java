package com.argo.core.web.view;

import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

/**
 * Created by yaming_deng on 2014/10/10.
 */
public class FreeMarkerResourceViewResolver extends FreeMarkerViewResolver {

    @Override
    protected Class<?> requiredViewClass() {
        return FreeMarkerResourceView.class;
    }
}

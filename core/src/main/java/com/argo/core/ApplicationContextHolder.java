package com.argo.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by yaming_deng on 14-8-29.
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware, InitializingBean {

    public static ApplicationContextHolder current;
    public ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        current = this;
    }
}

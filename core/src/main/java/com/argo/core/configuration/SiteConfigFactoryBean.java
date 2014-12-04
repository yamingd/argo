package com.argo.core.configuration;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by Yaming on 2014/12/4.
 */
public class SiteConfigFactoryBean implements FactoryBean<SiteConfig>, InitializingBean, DisposableBean {

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public SiteConfig getObject() throws Exception {
        return SiteConfig.instance;
    }

    @Override
    public Class<?> getObjectType() {
        return SiteConfig.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (SiteConfig.instance == null){
            new SiteConfig().afterPropertiesSet();
        }
    }
}

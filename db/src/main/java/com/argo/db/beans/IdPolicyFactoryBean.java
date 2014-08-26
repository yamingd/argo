package com.argo.db.beans;

import com.argo.core.base.BaseBean;
import com.argo.core.policy.IdGenPolicy;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by yaming_deng on 14-8-26.
 */
public class IdPolicyFactoryBean extends BaseBean implements FactoryBean<IdGenPolicy> {

    private IdGenPolicy current = null;

    @Override
    public IdGenPolicy getObject() throws Exception {
        return current;
    }

    @Override
    public Class<?> getObjectType() {
        return IdGenPolicy.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        instance = this;
    }

    public void setCurrent(IdGenPolicy current) {
        this.current = current;
    }

    public IdGenPolicy getCurrent() {
        return current;
    }

    public static IdPolicyFactoryBean instance;
}

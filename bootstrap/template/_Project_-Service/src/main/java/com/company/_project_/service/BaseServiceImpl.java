package com.company._project_.service;

import com.argo.db.template.ServiceMSTemplate;
import com.argo.redis.RedisBuket;
import com.argo.core.base.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by yaming_deng on 14-8-19.
 */
public abstract class BaseServiceImpl extends ServiceMSTemplate {

    @Autowired
    protected RedisBuket redisBuket;

}

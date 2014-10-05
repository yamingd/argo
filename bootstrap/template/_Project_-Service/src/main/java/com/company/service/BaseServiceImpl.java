package com.company.service;

import com.argo.db.template.ServiceMSTemplate;
import com.argo.redis.RedisBuket;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by yaming_deng on 14-8-19.
 */
public abstract class BaseServiceImpl<T extends BaseEntity> extends ServiceMSTemplate<T> {

    @Autowired
    protected RedisBuket redisBuket;

}

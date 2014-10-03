package com.argo.redis.impl;

import com.argo.core.base.BaseBean;
import com.argo.mail.EmailMessage;
import com.argo.mail.executor.EmailExecutor;
import com.argo.redis.RedisBuket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by yaming_deng on 14-8-27.
 */
@Component("redisEmailExecutor")
public class RedisEmailExecutor extends BaseBean implements EmailExecutor {

    @Autowired
    private RedisBuket redisBuket;

    private static final String queueNameM = "mail.qm";
    private static final String queueNameI = "mail.qi";

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
    }

    @Override
    public void add(EmailMessage message) {
        redisBuket.rpush(EmailMessage.class, queueNameM, message);
    }

    @Override
    public List<EmailMessage> dequeueMessage(int limit) {
        List<EmailMessage> resp = redisBuket.lpop(EmailMessage.class, queueNameM, limit);
        return resp;
    }


}

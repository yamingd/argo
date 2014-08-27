package com.argo.redis.impl;

import com.argo.core.base.BaseBean;
import com.argo.core.json.GsonUtil;
import com.argo.mail.EmailMessage;
import com.argo.mail.executor.EmailExecutor;
import com.argo.redis.RedisBuket;
import com.google.common.collect.Lists;
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
        redisBuket.rpush(queueNameM, GsonUtil.toJson(message));
    }

    @Override
    public List<EmailMessage> dequeueMessage(int limit) {
        List<String> resp = redisBuket.lpop(queueNameM, limit);
        List<EmailMessage> items = Lists.newArrayList();
        for(String item : resp){
            EmailMessage t = GsonUtil.asT(EmailMessage.class, item);
            items.add(t);
        }
        return items;
    }


}

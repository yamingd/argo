package com.argo.redis.impl;

import com.argo.core.base.BaseBean;
import com.argo.core.email.EmailItem;
import com.argo.core.email.EmailMessage;
import com.argo.core.email.executor.EmailExecutor;
import com.argo.core.email.service.EmailSenderService;
import com.argo.core.json.GsonUtil;
import com.argo.core.service.ServiceConfig;
import com.argo.redis.RedisBuket;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by yaming_deng on 14-8-27.
 */
@Component("redisEmailExecutor")
public class RedisEmailExecutor extends BaseBean implements EmailExecutor {

    @Autowired
    private RedisBuket redisBuket;

    private EmailSenderService emailSenderService;

    private static final String queueName = "email.q";
    private Integer batch = 10;
    private Integer interval = 1;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Map<String, String> cfg = ServiceConfig.instance.getMail();
        batch = Integer.parseInt(cfg.get("executors"));
        interval = Integer.parseInt(cfg.get("interval"));
        emailSenderService = this.serviceLocator.get(EmailSenderService.class);
        this.start();
    }

    @Override
    public void add(EmailItem item) {
        logger.error("@@DONT USE THIS. add(EmailItem item) @@");
    }

    @Override
    public void add(EmailMessage message) {
        redisBuket.rpush(queueName, GsonUtil.toJson(message));
    }

    @Override
    public void dequeueAndSend() {

        List<EmailMessage> items = Lists.newArrayList();
        for (int i = 0; i < batch; i++) {
            String json = redisBuket.lpop(queueName);
            if (StringUtils.isNotBlank(json)) {
                EmailMessage item = GsonUtil.asT(EmailMessage.class, json);
                items.add(item);
            }else{
                break;
            }
        }

        postSend(items);

    }

    protected void postSend(List<EmailMessage> items) {
        for (EmailMessage item : items){
            boolean flag = emailSenderService.send(item);
            if (!flag){
                item.setTryLimit(item.getTryLimit() - 1);
                if (item.getTryLimit() > 0){
                    add(item);
                }
            }
        }
    }

    public class PullThread extends Thread{

        @Override
        public void run() {

            while (true){

                dequeueAndSend();

                try {
                    Thread.sleep(interval * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void start() {
        new PullThread().start();
    }
}

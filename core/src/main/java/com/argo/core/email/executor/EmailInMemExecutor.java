package com.argo.core.email.executor;

import com.argo.core.base.BaseBean;
import com.argo.core.email.EmailItem;
import com.argo.core.email.EmailMessage;
import com.argo.core.email.service.EmailSenderService;
import com.argo.core.service.ServiceConfig;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by yaming_deng on 14-8-27.
 */
@Component("emailInMemExecutor")
public class EmailInMemExecutor extends BaseBean implements EmailExecutor {

    private ThreadPoolTaskExecutor executor;
    private EmailSenderService emailSenderService;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Map<String, String> cfg = ServiceConfig.instance.getMail();
        int limit = Integer.parseInt(cfg.get("executors"));

        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(limit / 10);
        executor.setMaxPoolSize(limit);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.afterPropertiesSet();

        emailSenderService = this.serviceLocator.get(EmailSenderService.class);
    }

    @Override
    public void destroy() throws Exception {
        super.destroy();
        executor.destroy();
    }

    @Override
    public void add(EmailItem item) {
        logger.error("@@DONT USE THIS. add(EmailItem item) @@");
    }

    public void add(final EmailMessage message){

        executor.submit(new Runnable() {
            @Override
            public void run() {

                emailSenderService.send(message);

            }
        });
    }

    @Override
    public void dequeueAndSend() {

    }

    @Override
    public void start() {

    }

}

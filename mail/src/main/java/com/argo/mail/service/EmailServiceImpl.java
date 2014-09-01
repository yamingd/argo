package com.argo.mail.service;

import com.argo.core.base.BaseBean;
import com.argo.mail.EmailMessage;
import com.argo.mail.executor.EmailExecutor;
import com.argo.mail.executor.EmailPostSender;
import com.argo.service.ServiceConfig;
import com.argo.service.annotation.RmiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 对外的统一接口.
 * Created by yaming_deng on 14-8-27.
 */
@RmiService(serviceInterface = EmailService.class)
public class EmailServiceImpl extends BaseBean implements EmailService {

    private static AtomicLong pending = new AtomicLong();

    private Integer batch = 10;
    private Integer interval = 1;
    private EmailExecutor executor;
    private ThreadPoolTaskExecutor pools;
    private volatile boolean stopping;

    @Autowired
    private EmailPostSender emailPostSender;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        stopping = false;

        Map<String, String> cfg = ServiceConfig.instance.getMail();
        String beanName = cfg.get("executor");
        if (StringUtils.isNotBlank(beanName)) {
            executor = this.applicationContext.getBean(beanName + "EmailExecutor", EmailExecutor.class);
        }

        batch = Integer.parseInt(cfg.get("batch"));
        interval = Integer.parseInt(cfg.get("interval"));

        pools = new ThreadPoolTaskExecutor();
        pools.setCorePoolSize(batch / 10);
        pools.setMaxPoolSize(batch);
        pools.setWaitForTasksToCompleteOnShutdown(true);
        pools.afterPropertiesSet();

        if (this.executor != null) {
            new PullThread().start();
        }

    }

    @Override
    public void destroy() throws Exception {
        this.stopping = true;
        super.destroy();
    }

    @Override
    public void add(final EmailMessage message) {
        if (executor != null) {
            executor.add(message);
        }else{

            long total = pending.incrementAndGet();
            logger.info("Pending Email to send. total = " + total);

            pools.submit(new Runnable() {
                @Override
                public void run() {
                    postSend(message);
                }
            });
        }
    }

    @Override
    public void send(final EmailMessage message) {
        if (this.stopping){
            executor.add(message);
            return;
        }

        long total = pending.incrementAndGet();
        logger.info("Pending Email to send. total = " + total);

        pools.submit(new Runnable() {
            @Override
            public void run() {
                postSend(message);
            }
        });
    }

    public class PullThread extends Thread{

        @Override
        public void run() {

            while (!stopping){
                List<EmailMessage> items = executor.dequeueMessage(batch);
                for (final EmailMessage item : items){
                    pending.incrementAndGet();
                    pools.submit(new Runnable() {
                        @Override
                        public void run() {
                            postSend(item);
                        }
                    });
                }

                try {
                    Thread.sleep(interval * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void postSend(final EmailMessage item) {
        boolean flag = emailPostSender.send(item);
        if (!flag){
            item.setTryLimit(item.getTryLimit() - 1);
            if (item.getTryLimit() > 0){
                add(item);
            }
        }
        long total = pending.decrementAndGet();
        logger.info("Pending Email to send. total = " + total);
    }

}

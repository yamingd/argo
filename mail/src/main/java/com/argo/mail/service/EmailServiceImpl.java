package com.argo.mail.service;

import com.argo.core.base.BaseBean;
import com.argo.mail.EmailMessage;
import com.argo.mail.executor.EmailExecutor;
import com.argo.mail.executor.EmailPostSender;
import com.argo.service.ServiceConfig;
import com.argo.service.annotation.RmiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
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
    private boolean enabled = false;

    @Autowired
    private EmailPostSender emailPostSender;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        stopping = false;

        Map<String, Object> cfg = ServiceConfig.instance.getMail();
        batch = cfg.get("batch") == null ? 10 : (Integer)cfg.get("batch");
        interval = cfg.get("interval") == null ? 1 : (Integer)cfg.get("interval");

        enabled = cfg.get("enabled") == null ? false : (Boolean)cfg.get("enabled");
        if (!enabled){
            logger.warn("EmailService has been disabled!");
            return;
        }

        pools = new ThreadPoolTaskExecutor();
        pools.setCorePoolSize(batch / 10);
        pools.setMaxPoolSize(batch);
        pools.setWaitForTasksToCompleteOnShutdown(true);
        pools.afterPropertiesSet();
    }

    @Override
    public void start(EmailExecutor executor) {
        if (!enabled){
            logger.warn("EmailService has been disabled!");
            return;
        }

        this.executor = executor;
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
        if (!enabled){
            logger.warn("EmailService has been disabled!");
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

    @Override
    public void send(final EmailMessage message) {
        if (!enabled){
            logger.warn("EmailService has been disabled!");
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

                try {
                    Thread.sleep(interval * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                List<EmailMessage> items = executor.dequeueMessage(batch);
                if (items.size() == 0){
                    continue;
                }

                final CountDownLatch latch = new CountDownLatch(items.size());
                for (final EmailMessage item : items){
                    pending.incrementAndGet();
                    pools.submit(new Runnable() {
                        @Override
                        public void run() {
                            postSend(item);
                            latch.countDown();
                        }
                    });
                }

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    protected void postSend(final EmailMessage item) {
        boolean flag = emailPostSender.send(item);
        if (!flag){
            item.tryLimit--;
            executor.callback(item, false);
        }else{
            executor.callback(item, true);
        }
        long total = pending.decrementAndGet();
        logger.info("Pending Email to send. total = " + total);
    }

}

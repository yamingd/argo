package com.argo.db.impl;

import com.argo.core.base.BaseBean;
import com.argo.core.email.EmailItem;
import com.argo.core.email.EmailMessage;
import com.argo.core.email.executor.EmailExecutor;
import com.argo.core.email.service.EmailSenderService;
import com.argo.core.service.ServiceConfig;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * Created by yaming_deng on 14-8-27.
 */
public class MySQLEmailExecutor extends BaseBean implements EmailExecutor {

    private EmailSenderService emailSenderService;
    private Integer batch = 10;
    private Integer interval = 1;

    private JdbcTemplate jdbcWriter;
    private JdbcTemplate jdbcRead;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Map<String, String> cfg = ServiceConfig.instance.getMail();
        batch = Integer.parseInt(cfg.get("executors"));
        interval = Integer.parseInt(cfg.get("interval"));
        emailSenderService = this.serviceLocator.get(EmailSenderService.class);

        String jtwriteName = cfg.get("jtw_name");
        String jtreadName = cfg.get("jtr_name");

        jdbcWriter = this.applicationContext.getBean(jtwriteName, JdbcTemplate.class);
        jdbcRead = this.applicationContext.getBean(jtreadName, JdbcTemplate.class);

        this.start();
    }

    @Override
    public void add(EmailItem item) {

    }

    @Override
    public void add(EmailMessage item) {

    }

    @Override
    public void dequeueAndSend() {

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

package com.argo.core.email.service;

import com.argo.core.base.BaseBean;
import com.argo.core.component.FreemarkerComponent;
import com.argo.core.email.EmailMessage;
import com.argo.core.metric.MetricCollectorImpl;
import com.argo.core.service.ServiceConfig;
import com.argo.core.service.annotation.RmiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 *
 * #mail config
   mail:
     host : "smtp.example.com"
     user : "test"
     passwd : "test12345"
     feedback : "feedback@example.com"
     failed_limit : 1
     domain : "@example.com"
     executors : 100
     auth : true
     timeout : 25000
 *
 * Created by yaming_deng on 14-8-27.
 */
@RmiService(serviceInterface = EmailSenderService.class)
public class EmailSenderServiceImpl extends BaseBean implements EmailSenderService {

    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_SUCCESS = "success";
    private JavaMailSenderImpl mailSender = null;

    @Autowired
    private FreemarkerComponent freemarkerComponent;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Map<String, String> cfg = ServiceConfig.instance.getMail();

        mailSender = new JavaMailSenderImpl();
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setHost(cfg.get("host"));
        mailSender.setUsername(cfg.get("user"));
        mailSender.setPassword(cfg.get("passwd"));
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", cfg.get("auth"));
        props.setProperty("mail.smtp.timeout", cfg.get("timeout"));
        mailSender.setJavaMailProperties(props);

    }

    @Override
    public boolean send(EmailMessage emailMessage) {

        Assert.notNull(emailMessage.getTitle(), "emailMessage.title should not be null");

        if (StringUtils.isBlank(emailMessage.getBody())){
            Assert.notNull(emailMessage.getBodyFtl(), "emailMessage.bodyFtl should not be null.");
            try {
                String body = freemarkerComponent.render(emailMessage.getBodyFtl(), emailMessage.getParams());
                emailMessage.setBody(body);
            } catch (Exception e) {
                logger.error("邮件模板处理错误! 邮件为: " + emailMessage.getId() + ", ftl=" + emailMessage.getBodyFtl(), e);
                return false;
            }
        }

        try {

            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper message = null;
            if (emailMessage.getAttachments() != null && emailMessage.getAttachments().size() > 0) {
                message = new MimeMessageHelper(msg, true, "UTF-8");
            }else{
                message = new MimeMessageHelper(msg, false, "UTF-8");
            }

            message.setFrom(emailMessage.getFromAddress());
            message.setSubject(emailMessage.getTitle());
            message.setTo(emailMessage.getToAddress().toArray(new String[0]));

            if (emailMessage.getCcAddress() != null && emailMessage.getCcAddress().size() > 0){
                message.setCc(emailMessage.getCcAddress().toArray(new String[0]));
            }
            if (emailMessage.getBccAddress() != null && emailMessage.getBccAddress().size() > 0){
                message.setBcc(emailMessage.getBccAddress().toArray(new String[0]));
            }

            message.setText(emailMessage.getBody(), emailMessage.getFormat().equalsIgnoreCase("html"));

            if (emailMessage.getAttachments() != null) {
                Iterator<String> itor = emailMessage.getAttachments().keySet().iterator();
                while (itor.hasNext()){
                    String name = itor.next();
                    String filePath = emailMessage.getAttachments().get(name);
                    message.addAttachment(name, new File(filePath));
                }
            }

            mailSender.send(msg);

        } catch (MessagingException e) {
            logger.error("邮件信息导常! 邮件为: " + emailMessage.getId(), e);
            MetricCollectorImpl.current().markMeter(this.getClass(), STATUS_FAILED);
            return false;
        } catch (MailException me) {
            logger.warn("发送邮件失败! 邮件为: " + emailMessage.getId(), me);
            MetricCollectorImpl.current().markMeter(this.getClass(), STATUS_FAILED);
            return false;
        }
        if (logger.isDebugEnabled()){
            logger.debug("邮件发送成功. id=" + emailMessage.getId());
        }

        MetricCollectorImpl.current().markMeter(this.getClass(), STATUS_SUCCESS);

        return true;
    }

}

package com.argo.mail.executor;

import com.argo.core.base.BaseBean;
import com.argo.core.component.FreemarkerComponent;
import com.argo.core.metric.MetricCollectorImpl;
import com.argo.mail.EmailMessage;
import com.argo.service.ServiceConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created by yaming_deng on 14-8-27.
 */
@Component
public class EmailPostSender extends BaseBean {

    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_SUCCESS = "success";
    private JavaMailSenderImpl mailSender = null;

    @Autowired
    private FreemarkerComponent freemarkerComponent;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Map<String, Object> cfg = ServiceConfig.instance.getMail();

        mailSender = new JavaMailSenderImpl();
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setHost(cfg.get("host") + "");
        mailSender.setUsername(cfg.get("user") + "");
        mailSender.setPassword(cfg.get("passwd") + "");
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", cfg.get("auth") + "");
        props.setProperty("mail.smtp.timeout", cfg.get("timeout") + "");
        mailSender.setJavaMailProperties(props);

    }

    public boolean send(EmailMessage emailMessage) {

        Assert.notNull(emailMessage.title, "emailMessage.title should not be null");

        if (StringUtils.isBlank(emailMessage.body)){
            Assert.notNull(emailMessage.bodyFtl, "emailMessage.bodyFtl should not be null.");
            try {
                String body = freemarkerComponent.render(emailMessage.bodyFtl, emailMessage.params);
                emailMessage.body = body;
            } catch (Exception e) {
                logger.error("邮件模板处理错误! 邮件为: " + emailMessage.id + ", ftl=" + emailMessage.bodyFtl, e);
                return false;
            }
        }

        try {

            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper message = null;
            if (emailMessage.attachments != null && emailMessage.attachments.size() > 0) {
                message = new MimeMessageHelper(msg, true, "UTF-8");
            }else{
                message = new MimeMessageHelper(msg, false, "UTF-8");
            }

            message.setFrom(emailMessage.fromAddress);
            message.setSubject(emailMessage.title);
            message.setTo(emailMessage.toAddress.toArray(new String[0]));

            if (emailMessage.ccAddress != null && emailMessage.ccAddress.size() > 0){
                message.setCc(emailMessage.ccAddress.toArray(new String[0]));
            }
            if (emailMessage.bccAddress != null && emailMessage.bccAddress.size() > 0){
                message.setBcc(emailMessage.bccAddress.toArray(new String[0]));
            }

            message.setText(emailMessage.body, emailMessage.format.equalsIgnoreCase("html"));

            if (emailMessage.attachments != null) {
                Iterator<String> itor = emailMessage.attachments.keySet().iterator();
                while (itor.hasNext()){
                    String name = itor.next();
                    String filePath = emailMessage.attachments.get(name);
                    message.addAttachment(name, new File(filePath));
                }
            }

            mailSender.send(msg);

        } catch (MessagingException e) {
            logger.error("邮件信息导常! 邮件为: " + emailMessage.id, e);
            MetricCollectorImpl.current().markMeter(this.getClass(), STATUS_FAILED);
            return false;
        } catch (MailException me) {
            logger.warn("发送邮件失败! 邮件为: " + emailMessage.id, me);
            MetricCollectorImpl.current().markMeter(this.getClass(), STATUS_FAILED);
            return false;
        }
        if (logger.isDebugEnabled()){
            logger.debug("邮件发送成功. id=" + emailMessage.id);
        }

        MetricCollectorImpl.current().markMeter(this.getClass(), STATUS_SUCCESS);

        return true;
    }

}

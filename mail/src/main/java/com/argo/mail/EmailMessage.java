package com.argo.mail;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.msgpack.annotation.Ignore;
import org.msgpack.annotation.Message;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by yaming_deng on 14-8-27.
 */
@Message
public class EmailMessage implements Serializable {

    public static final String FORMAT_HTML = "html";
    public static final String FORMAT_TXT = "txt";

    public Long id;
    public String category;

    public String title;
    public String body;
    public String bodyFtl;
    public String format; //txt, html
    public String fromAddress;
    public List<String> toAddress;
    public List<String> ccAddress;
    public List<String> bccAddress;
    public Map<String, String> attachments;

    @Ignore
    public Map<String, Object> params;

    public Integer tryLimit = 3;

    public EmailMessage() {
        this.params = Maps.newHashMap();
        this.toAddress = Lists.newArrayList();
        this.ccAddress = Lists.newArrayList();
        this.bccAddress = Lists.newArrayList();
        this.attachments = Maps.newHashMap();
        this.tryLimit = 3;
        this.format = "html";
    }

    public EmailMessage(Long id) {
        this.id = id;
        this.params = Maps.newHashMap();
        this.toAddress = Lists.newArrayList();
        this.ccAddress = Lists.newArrayList();
        this.bccAddress = Lists.newArrayList();
        this.attachments = Maps.newHashMap();
        this.tryLimit = 3;
        this.format = "html";
    }

    public static EmailMessage text(){
        EmailMessage message = new EmailMessage();
        message.format = FORMAT_TXT;
        return message;
    }

    public static EmailMessage html(){
        EmailMessage message = new EmailMessage();
        message.format = FORMAT_HTML;
        return message;
    }
}

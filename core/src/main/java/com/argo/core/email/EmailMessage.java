package com.argo.core.email;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by yaming_deng on 14-8-27.
 */
public class EmailMessage implements Serializable {

    public static final String FORMAT_HTML = "html";
    public static final String FORMAT_TXT = "txt";

    private Long id;
    private String category;

    private String title;
    private String body;
    private String bodyFtl;
    private String format; //txt, html
    private String fromAddress;
    private List<String> toAddress;
    private List<String> ccAddress;
    private List<String> bccAddress;
    private Map<String, String> attachments;

    private Map<String, Object> params;
    private Integer tryLimit = 3;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBodyFtl() {
        return bodyFtl;
    }

    public void setBodyFtl(String bodyFtl) {
        this.bodyFtl = bodyFtl;
    }

    public List<String> getToAddress() {
        return toAddress;
    }

    public void setToAddress(List<String> toAddress) {
        this.toAddress = toAddress;
    }

    public List<String> getCcAddress() {
        return ccAddress;
    }

    public void setCcAddress(List<String> ccAddress) {
        this.ccAddress = ccAddress;
    }

    public List<String> getBccAddress() {
        return bccAddress;
    }

    public void setBccAddress(List<String> bccAddress) {
        this.bccAddress = bccAddress;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

    public Integer getTryLimit() {
        return tryLimit;
    }

    public void setTryLimit(Integer tryLimit) {
        this.tryLimit = tryLimit;
    }
}

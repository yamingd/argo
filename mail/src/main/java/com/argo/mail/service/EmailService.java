package com.argo.mail.service;


import com.argo.mail.EmailMessage;

/**
 * Created by yaming_deng on 14-8-27.
 */
public interface EmailService {

    /**
     * 先进队列.
     * @param message
     */
    void add(EmailMessage message);

    /**
     * 立刻发送
     * @param message
     */
    void send(EmailMessage message);
}

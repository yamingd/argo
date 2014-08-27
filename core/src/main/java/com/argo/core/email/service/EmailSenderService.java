package com.argo.core.email.service;

import com.argo.core.email.EmailMessage;

/**
 * Created by yaming_deng on 14-8-27.
 */
public interface EmailSenderService {

    boolean send(EmailMessage emailMessage);

}

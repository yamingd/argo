package com.argo.mail.executor;


import com.argo.mail.EmailMessage;

import java.util.List;

/**
 * Created by yaming_deng on 14-8-27.
 */
public interface EmailExecutor {
    /**
     *
     * @param message
     */
    void callback(final EmailMessage message, boolean result);
    /**
     *
     * @param limit
     * @return
     */
    List<EmailMessage> dequeueMessage(int limit);
}

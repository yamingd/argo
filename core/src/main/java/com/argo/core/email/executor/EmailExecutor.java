package com.argo.core.email.executor;

import com.argo.core.email.EmailItem;
import com.argo.core.email.EmailMessage;

/**
 * Created by yaming_deng on 14-8-27.
 */
public interface EmailExecutor {

    /**
     *
     * @param item
     */
    void add(final EmailItem item);
    /**
     *
     * @param message
     */
    void add(final EmailMessage message);

    /**
     *
     * @return
     */
    void dequeueAndSend();

    /**
     *
     */
    void start();
}

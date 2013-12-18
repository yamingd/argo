package com.argo.message;

import com.argo.core.base.BaseException;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-1
 * Time: 上午8:12
 */
public class MessageException extends BaseException {

    public MessageException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
    }

    public MessageException(String message, Object... params) {
        super(message, params);
    }

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageException(String message) {
        super(message);
    }
}

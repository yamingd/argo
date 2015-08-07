package com.argo.core.exception;

import com.argo.core.base.BaseException;

/**
 * Created by yamingd on 8/7/15.
 */
public class CookieInvalidException extends BaseException {

    public CookieInvalidException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
    }

    public CookieInvalidException(String message, Object... params) {
        super(message, params);
    }

    public CookieInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public CookieInvalidException(String message) {
        super(message);
    }
}

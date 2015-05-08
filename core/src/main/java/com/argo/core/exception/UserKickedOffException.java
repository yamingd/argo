package com.argo.core.exception;

/**
 * Created by user on 5/8/15.
 */
public class UserKickedOffException extends ServiceException {

    public UserKickedOffException(String message) {
        super(message);
        this.setErrcode(60900);
    }
}

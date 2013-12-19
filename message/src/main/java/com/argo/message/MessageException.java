package com.argo.message;


/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-1
 * Time: 上午8:12
 */
public class MessageException extends Exception {

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageException(String message) {
        super(message);
    }
}

package com.argo.redis;

import org.msgpack.annotation.Message;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Yaming
 * Date: 2014/10/3
 * Time: 22:50
 */
@Message
public class Person {

    public Long id;
    public String name;
    public Date createAt;
}

package com.argo.core.web;

import com.argo.core.base.BaseUser;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-22
 * Time: 下午1:49
 */
public class AnonymousUser extends BaseUser {

    public AnonymousUser() {
        this.setUid(0L);
        this.setLoginId("Anonymous");
    }
}

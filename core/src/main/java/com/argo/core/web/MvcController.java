package com.argo.core.web;

import com.argo.core.service.factory.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-15
 * Time: 下午8:12
 */
public abstract class MvcController {

    @Autowired
    protected ServiceLocator serviceLocator;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

}

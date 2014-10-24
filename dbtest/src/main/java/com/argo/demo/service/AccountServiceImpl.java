package com.argo.demo.service;

import com.argo.core.annotation.Model;
import com.argo.db.template.ServiceMSTemplate;
import com.argo.demo.Person;
import com.argo.service.annotation.RmiService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Yaming on 2014/10/24.
 */
@Model(Person.class)
@RmiService(serviceInterface = AccountService.class)
public class AccountServiceImpl extends ServiceMSTemplate implements AccountService  {

    @Autowired
    private PersonService personService;

}

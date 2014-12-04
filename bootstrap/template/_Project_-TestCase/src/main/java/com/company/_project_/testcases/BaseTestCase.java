package com.company._project_.testcases;

import com.argo.core.ContextConfig;
import com.argo.runner.RestAPITestRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.http.client.methods.HttpUriRequest;
/**
 * Created with IntelliJ IDEA.
 * User: Yaming
 * Date: 2014/10/6
 * Time: 10:10
 */
public abstract class BaseTestCase extends RestAPITestRunner {

    public static ClassPathXmlApplicationContext context;
    public static String currentUserId;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.setProperty(ContextConfig.RUNNING_ENV, "test");
        context = new ClassPathXmlApplicationContext("spring/root-context.xml");
    }

    protected void configHttpHeader(HttpUriRequest request){
        //TODO: implement this in subclass
    }

    protected String getCurrentUserId(){
        return currentUserId;
    }
}

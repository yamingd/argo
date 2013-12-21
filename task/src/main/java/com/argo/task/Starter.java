package com.argo.task;

import com.argo.task.factory.DefaultTaskSchedulerFactoryBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-20
 * Time: 下午9:32
 */
public class Starter {

    public void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/spring/rootContext.xml");
        DefaultTaskSchedulerFactoryBean bean = context.getBean("taskSchedulerFactory", DefaultTaskSchedulerFactoryBean.class);
        while (true){
            try {
                Thread.sleep(1000 * 60 * 60);
            } catch (InterruptedException e) {

            }
            System.out.println(new Date());
        }
    }

}

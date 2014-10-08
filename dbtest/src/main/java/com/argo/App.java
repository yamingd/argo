package com.argo;

import com.argo.core.exception.ServiceException;
import com.argo.demo.Person;
import com.argo.demo.service.PersonService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * http://galeracluster.com/documentation-webpages/dbconfiguration.html#
 *
 */
public class App 
{
    private static PersonService personService;

    public static void main( String[] args )
    {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/root-context.xml");
        personService = context.getBean(PersonService.class);

        testAdd(8, 10000);
    }

    public static void testAdd(int threads, final int limit){
        final AtomicLong duration = new AtomicLong();

        for (int j=0; j<threads; j++){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    long ts0 = System.currentTimeMillis();
                    for (int i=0; i<limit; i++){
                        Person person = new Person();
                        person.setName("Person:" + new Date().getTime());
                        try {
                            personService.add(person);
                        } catch (ServiceException e) {
                            e.printStackTrace();
                        }
                    }
                    long ts = System.currentTimeMillis() - ts0;
                    duration.addAndGet(ts);
                    System.out.println("ts: " + ts);
                }
            });

            thread.start();

        }

        try {
            Thread.sleep(600 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("duration: " + duration.get());

    }
}

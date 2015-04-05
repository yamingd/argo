package com.argo;

import com.argo.tools.dbm.TableService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/root-context.xml");
        TableService tableService = context.getBean(TableService.class);
        tableService.exportXls("career_dev", "simple");
        tableService.exportXls("career_dev", "full");
    }
}

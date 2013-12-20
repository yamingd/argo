package com.argo.core;

import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-11-25
 * Time: 下午9:48
 */
public class ContextConfig {

    public static final String RUNNING_ENV = "com.argo.running.env";

    public static void setRunning(String env){
        System.setProperty(RUNNING_ENV, env);
    }

    public static String getRunning(){
        String env = System.getProperty(RUNNING_ENV);
        if(env == null || env.trim().length() == 0){
            env = "dev";
        }
        return env;
    }

    public static boolean isDev(){
        return "dev".equalsIgnoreCase(getRunning());
    }

    public static boolean isTest(){
        return "test".equalsIgnoreCase(getRunning());
    }

    public static boolean isProduction(){
        return "prod".equalsIgnoreCase(getRunning());
    }

    public static void set(String key, String value){
        System.setProperty(key, value);
    }

    public static String get(String key){
        return System.getProperty(key);
    }

    public static void init(ServletContext context){
        Enumeration<String> itor = context.getInitParameterNames();
        while (itor.hasMoreElements()){
            String key = itor.nextElement();
            String val = context.getInitParameter(key);
            set(key, val);
        }
    }
}

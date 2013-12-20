package com.argo.message;

import com.argo.core.metric.MetricCollectorImpl;
import com.codahale.metrics.Timer;

/**
 * Created by yaming_deng on 13-12-20.
 */
public class MessageMetric {

    public static final void publishIncr(Class<?> clazz, String queue, String op){
        String name = queue+":"+op+":";
        MetricCollectorImpl.current().incrementCounter(clazz, name);
    }

    public static final void consumeIncr(Class<?> clazz, String queue, String op){
        String name = queue+":"+op+":";
        MetricCollectorImpl.current().incrementCounter(clazz, name);
    }

    public static final void consumeFailedIncr(Class<?> clazz, String queue, String op){
        String name = queue+":"+op+":";
        MetricCollectorImpl.current().incrementCounter(clazz, name);
    }

    public static final Timer.Context consumerTimer(Class<?> clazz, String queue, String op){
        String name = queue+":"+op;
        return MetricCollectorImpl.current().getTimer(clazz, name);
    }

    public static final Timer.Context consumerTimer(Class<?> clazz, String queue){
        String name = queue;
        return MetricCollectorImpl.current().getTimer(clazz, name);
    }
}

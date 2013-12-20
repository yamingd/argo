package com.argo.message;

import com.argo.core.metric.MetricCollectorImpl;
import com.codahale.metrics.Timer;

/**
 * Created by yaming_deng on 13-12-20.
 */
public class MessageMetric {

    private String queue;
    private Class<?> clazz;

    public MessageMetric(Class<?> clazz, String queue) {
        this.clazz = clazz;
        this.queue = queue;
    }

    public final void publishIncr(String queue, String op){
        String name = queue+":"+op+":";
        MetricCollectorImpl.current().incrementCounter(clazz, name);
    }

    public final void consumeIncr(String op){
        String name = queue+":"+op+":";
        MetricCollectorImpl.current().incrementCounter(clazz, name);
    }

    public final void consumeFailedIncr(String op){
        String name = queue+":"+op+":";
        MetricCollectorImpl.current().incrementCounter(clazz, name);
    }

    public final Timer.Context consumerTimer(String op){
        String name = queue+":"+op;
        return MetricCollectorImpl.current().getTimer(clazz, name);
    }

    public final Timer.Context publisherTimer(String queue, String op){
        String name = queue+":"+op;
        return MetricCollectorImpl.current().getTimer(clazz, name);
    }
}

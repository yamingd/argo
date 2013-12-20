package com.argo.message;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Created by yaming_deng on 13-12-20.
 */
public class MessageMetric {

    static final MetricRegistry metrics = new MetricRegistry();

    public static final void publishIncr(Class<?> clazz, String queue, String op){
        String name = queue+":"+op+":";
        metrics.counter(MetricRegistry.name(
                clazz, name)).inc();
    }

    public static final void consumeIncr(Class<?> clazz, String queue, String op){
        String name = queue+":"+op+":";
        metrics.counter(MetricRegistry.name(
                clazz, name)).inc();
    }

    public static final void consumeFailedIncr(Class<?> clazz, String queue, String op){
        String name = queue+":"+op+":";
        metrics.counter(MetricRegistry.name(
                clazz, name)).inc();
    }

    public static final Timer.Context consumerTimer(Class<?> clazz, String queue, String op){
        String name = queue+":"+op+":ts";
        final Timer timer = metrics.timer(MetricRegistry.name(
                clazz, name));
        return timer.time();
    }
    public static final Timer.Context consumerTimer(Class<?> clazz, String queue){
        String name = queue+":ts";
        final Timer timer = metrics.timer(MetricRegistry.name(
                clazz, name));
        return timer.time();
    }
}

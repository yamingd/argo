package com.argo.search;

import com.argo.core.metric.MetricCollectorImpl;
import com.codahale.metrics.Timer;

/**
 * Created by yamingd on 13-12-20.
 */
public class SearchMetrics {

    private Class<?> clazz;
    private String index;

    public SearchMetrics(Class<?> clazz, String index) {
        this.clazz = clazz;
        this.index = index;
    }

    public final void indexIncr(String type, String op){
        String name = index+":"+type+":"+op;
        MetricCollectorImpl.current().incrementCounter(clazz, name);
    }

    public final Timer.Context indexTimer(String type, String op){
        String name = index+":"+type+":"+op+":ts";
        return MetricCollectorImpl.current().getTimer(clazz, name);
    }

    public final void failedIncr(String type, String op, Exception e){
        String name = index+":"+type+":"+op+":"+e.getClass().getSimpleName();
        MetricCollectorImpl.current().incrementCounter(clazz, name);
    }


}

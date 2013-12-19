package com.argo.couchbase;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Created by yaming_deng on 13-12-19.
 */
public class CouchbaseMetric {

    static final MetricRegistry metrics = new MetricRegistry();

    public static final Timer queryViews = metrics.timer(MetricRegistry.name(
            CouchbaseTemplate.class, "findByView"));

    public static final Timer findByIds = metrics.timer(MetricRegistry.name(
            CouchbaseTemplate.class, "findByIds"));

    public static final Timer statViews = metrics.timer(MetricRegistry.name(
            CouchbaseTemplate.class, "statView"));

    public static final void callCountIncr(String name){
        metrics.counter(MetricRegistry.name(
                CouchbaseTemplate.class, name+"C")).inc();
    }
}

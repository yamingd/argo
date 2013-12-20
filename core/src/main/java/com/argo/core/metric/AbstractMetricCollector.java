package com.argo.core.metric;

/**
 * Created by yamingd on 13-12-20.
 */
public abstract class AbstractMetricCollector  implements MetricCollector {

    @Override
    public void decrementCounter(Class<?> clazz, String name) {
        decrementCounter(clazz, name, 1);
    }

    @Override
    public void incrementCounter(Class<?> clazz, String name) {
        incrementCounter(clazz, name, 1);
    }

}

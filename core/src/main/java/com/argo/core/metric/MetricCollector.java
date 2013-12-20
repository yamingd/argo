package com.argo.core.metric;

import com.codahale.metrics.Timer;

/**
 * Created by yamingd on 13-12-20.
 */
public interface MetricCollector {
    /**
     * Add a Counter to the collector.
     *
     * @param name the name of the counter.
     */
    void addCounter(Class<?> clazz, String name);

    /**
     * Remove a Counter from the collector.
     *
     * @param name the name of the counter.
     */
    void removeCounter(Class<?> clazz, String name);

    /**
     * Increment a Counter by one.
     *
     * @param name the name of the counter.
     */
    void incrementCounter(Class<?> clazz, String name);

    /**
     * Increment a Counter by the given amount.
     *
     * @param name the name of the counter.
     * @param amount the amount to increase.
     */
    void incrementCounter(Class<?> clazz, String name, int amount);

    /**
     * Decrement a Counter by one.
     *
     * @param name the name of the counter.
     */
    void decrementCounter(Class<?> clazz, String name);

    /**
     * Decrement a Counter by the given amount.
     *
     * @param name the name of the counter.
     * @param amount the amount to decrease.
     */
    void decrementCounter(Class<?> clazz, String name, int amount);

    /**
     * Add a Meter to the Collector.
     *
     * @param name the name of the counter.
     */
    void addMeter(Class<?> clazz, String name);

    /**
     * Remove a Meter from the Collector.
     *
     * @param name the name of the counter.
     */
    void removeMeter(Class<?> clazz, String name);

    /**
     * Mark a checkpoint in the Meter.
     *
     * @param name the name of the counter.
     */
    void markMeter(Class<?> clazz, String name);

    /**
     * Add a Histogram to the Collector.
     *
     * @param name the name of the counter.
     */
    void addHistogram(Class<?> clazz, String name);

    /**
     * Remove a Histogram from the Collector.
     *
     * @param name the name of the counter.
     */
    void removeHistogram(Class<?> clazz, String name);

    /**
     * Update the Histogram with the given amount.
     *
     * @param name the name of the counter.
     * @param amount the amount to update.
     */
    void updateHistogram(Class<?> clazz, String name, int amount);
    /**
     * Add a Timer to the Collector.
     *
     * @param name the name of the counter.
     */
    Timer.Context getTimer(Class<?> clazz, String name);

    /**
     * Remove a Timer from the Collector.
     *
     * @param name the name of the counter.
     */
    void removeTimer(Class<?> clazz, String name);
}

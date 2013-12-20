package com.argo.core.metric;

import com.codahale.metrics.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by yamingd on 13-12-20.
 */
public class MetricCollectorImpl extends AbstractMetricCollector implements InitializingBean {
    /**
     * Use the "console" reporter by default.
     */
    public static final String DEFAULT_REPORTER_TYPE = "console";

    /**
     * Log every 30 seconds to the console by default.
     */
    public static final String DEFAULT_REPORTER_INTERVAL = "30";

    /**
     * Define an empty directory for the CSV exporter by default.
     */
    public static final String DEFAULT_REPORTER_OUTDIR = "";

    /**
     * Holds the registry where all metrics are stored.
     */
    private MetricRegistry registry;

    private ConcurrentHashMap<String, Timer> timers;
    /**
     * Contains all registered {@link com.codahale.metrics.Counter}s.
     */
    private ConcurrentHashMap<String, Counter> counters;

    /**
     * Contains all registered {@link com.codahale.metrics.Meter}s.
     */
    private ConcurrentHashMap<String, Meter> meters;

    /**
     * Contains all registered {@link com.codahale.metrics.Histogram}s.
     */
    private ConcurrentHashMap<String, Histogram> histograms;

    private Map<String, String> config = null;
    /**
     * Create a new {@link MetricCollectorImpl}.
     *
     * Note that when this constructor is called, the reporter is also
     * automatically established.
     */
    public MetricCollectorImpl(Map<String, String> config) {
        registry = new MetricRegistry();
        counters = new ConcurrentHashMap<String, Counter>();
        meters = new ConcurrentHashMap<String, Meter>();
        histograms = new ConcurrentHashMap<String, Histogram>();
        timers = new ConcurrentHashMap<String, Timer>();
        this.config = config;
        initReporter();
    }

    public MetricCollectorImpl() {
        this(null);
    }

    private String getConfigValue(String key, String defaultValue){
        if (this.config == null){
            return defaultValue;
        }
        String value = this.config.get(key);
        if(StringUtils.isBlank(value)){
            return defaultValue;
        }
        return value;
    }

    /**
     * Initialize the proper metrics Reporter.
     */
    private void initReporter() {
        String reporterType = getConfigValue("type", DEFAULT_REPORTER_TYPE);
                // System.getProperty("com.argo.metrics.reporter.type", DEFAULT_REPORTER_TYPE);
        String reporterInterval = getConfigValue("interval", DEFAULT_REPORTER_INTERVAL);
                //System.getProperty("com.argo.metrics.reporter.interval", DEFAULT_REPORTER_INTERVAL);
        String reporterDir = getConfigValue("outdir", DEFAULT_REPORTER_OUTDIR);
                //System.getProperty("com.argo.metrics.reporter.outdir", DEFAULT_REPORTER_OUTDIR);

        if(reporterType.equals("console")) {
            final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.SECONDS)
                    .build();
            reporter.start(Integer.parseInt(reporterInterval), TimeUnit.SECONDS);
        } else if (reporterType.equals("jmx")) {
            final JmxReporter reporter = JmxReporter.forRegistry(registry)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.SECONDS)
                    .build();
            reporter.start();
        } else if (reporterType.equals("csv")) {
            final CsvReporter reporter = CsvReporter.forRegistry(registry)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.SECONDS)
                    .build(new File(reporterDir));
            reporter.start(Integer.parseInt(reporterInterval), TimeUnit.SECONDS);
        } else if (reporterType.equals("slf4j")) {
            final Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.SECONDS)
                    .outputTo(LoggerFactory.getLogger(MetricCollector.class))
                    .build();
            reporter.start(Integer.parseInt(reporterInterval), TimeUnit.SECONDS);
        } else {
            throw new IllegalStateException("Unknown Metrics Reporter Type: " + reporterType);
        }
    }

    @Override
    public void addCounter(Class<?> clazz, String name) {
        name = clazz.getName()+"."+name;
        if (!counters.containsKey(name)) {
            counters.put(name, registry.counter(name));
        }
    }

    @Override
    public void removeCounter(Class<?> clazz, String name) {
        name = clazz.getName()+"."+name;
        if (!counters.containsKey(name)) {
            registry.remove(name);
            counters.remove(name);
        }
    }

    @Override
    public void incrementCounter(Class<?> clazz, String name, int amount) {
        this.addCounter(clazz, name);
        name = clazz.getName()+"."+name;
        counters.get(name).inc(amount);
    }

    @Override
    public void decrementCounter(Class<?> clazz, String name, int amount) {
        this.addCounter(clazz, name);
        name = clazz.getName()+"."+name;
        counters.get(name).dec(amount);
    }

    @Override
    public void addMeter(Class<?> clazz, String name) {
        name = clazz.getName()+"."+name;
        if (!meters.containsKey(name)) {
            meters.put(name, registry.meter(name));
        }
    }

    @Override
    public void removeMeter(Class<?> clazz, String name) {
        name = clazz.getName()+"."+name;
        if (meters.containsKey(name)) {
            meters.remove(name);
        }
    }

    @Override
    public void markMeter(Class<?> clazz, String name) {
        name = clazz.getName()+"."+name;
        if (meters.containsKey(name)) {
            meters.get(name).mark();
        }
    }

    @Override
    public void addHistogram(Class<?> clazz, String name) {
        name = clazz.getName()+"."+name;
        if (!histograms.containsKey(name)) {
            histograms.put(name, registry.histogram(name));
        }
    }

    @Override
    public void removeHistogram(Class<?> clazz, String name) {
        name = clazz.getName()+"."+name;
        if (histograms.containsKey(name)) {
            histograms.remove(name);
        }
    }

    @Override
    public void updateHistogram(Class<?> clazz, String name, int amount) {
        this.addHistogram(clazz, name);
        name = clazz.getName()+"."+name;
        if (histograms.containsKey(name)) {
            histograms.get(name).update(amount);
        }
    }

    @Override
    public Timer.Context getTimer(Class<?> clazz, String name) {
        name = clazz.getName()+"."+name+".ts";
        if(timers.contains(name)){
            return timers.get(name).time();
        }else{
            Timer timer = registry.timer(name);
            timers.put(name, timer);
            return timer.time();
        }
    }

    @Override
    public void removeTimer(Class<?> clazz, String name) {
        name = clazz.getName()+"."+name+".ts";
        registry.remove(name);
        timers.remove(name);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        _instance = this;
    }

    private static MetricCollectorImpl _instance = null;
    public static MetricCollectorImpl current(){
        if (_instance == null){
            _instance = new MetricCollectorImpl();
        }
        return _instance;
    }
}

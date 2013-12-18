package com.argo.search.elasticsearch;

/**
 * http://untergeek.com/2012/09/20/using-templates-to-improve-elasticsearch-caching-with-logstash/
 * http://jprante.github.io/2012/11/28/Elasticsearch-Java-Virtual-Machine-settings-explained.html
 * http://blogs.justenougharchitecture.com/?p=813
 * https://gist.github.com/duydo/2427158
 * http://blog.trifork.com/2013/09/26/maximum-shard-size-in-elasticsearch/
 * 
 * Sizing the heap based on application memory utilization
 * Set the maximum Java heap size, using the -Xmx command-line option, 
 * to a value that allows the application to run with 70% occupancy of the Java heap.
 * The Java heap occupancy often varies over time as the load applied to the application varies. 
 * For applications where occupancy varies, 
 * set the maximum Java heap size so that there is 70% occupancy at the highest point, 
 * and set the minimum heap size, using the -Xms command line option, 
 * so that the Java heap is 40% occupied at its lowest memory usage. 
 * If these values are set, the Java memory management algortihms can modify the heap size over time according 
 * to the application load, while maintaining usage in the optimal area of between 40% and 70% occupancy.
 * 
 * @author yaming_deng
 *
 */
public class tips {
	
}

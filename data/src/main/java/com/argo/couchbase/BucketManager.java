package com.argo.couchbase;

import com.argo.couchbase.exception.BucketException;
import com.argo.couchbase.exception.BucketNotFoundException;
import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.couchbase.client.CouchbaseConnectionFactoryBuilder;
import net.spy.memcached.FailureMode;
import net.spy.memcached.metrics.MetricType;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.*;

/**
 * <p>
 * Configuration at couchbase.yaml
 * </p>
 * 
 * <pre>
 *   metrics:
 *     type: jmx, csv
 *     out: /home/couchbase/client.metrics.csv
 *     interval: 30
 *   buckets:
 *     -bucket: $name1
 *      user: $user1
 *      pwd:  $pwd1
 *      urls: http://127.0.0.1:8091/pools,http://127.0.0.2:8091/pools
 *      ents: e1,e2,e3
 *     -bucket: $name2
 *      user: $user2
 *      pwd:  $pwd2
 *      urls: http://127.0.0.1:8091/pools,http://127.0.0.2:8091/pools
 *      ents: e4,e5,e6
 * </pre>
 * @author yaming_deng
 *
 */
@Component
public class BucketManager implements InitializingBean {
	
	public static final Logger logger = LoggerFactory.getLogger(BucketManager.class);
	
	public static BucketManager instance = null;
	public static final int EXP_DELETED = 3600;
	public static final Collection<String> ITERABLE_CLASSES;
	
	private Map<String, CouchbaseClient> buckets = null;
	private Map<String, String> entityBuckets = null;
	private String defaultBucket = null;

	@Autowired
	private BucketConfig config;
	
	static {
		final Set<String> iterableClasses = new HashSet<String>();
		iterableClasses.add(List.class.getName());
		iterableClasses.add(Collection.class.getName());
		iterableClasses.add(Iterator.class.getName());
		ITERABLE_CLASSES = Collections.unmodifiableCollection(iterableClasses);
	}
		  
	public CouchbaseClient get(String name) throws BucketNotFoundException {
		if (buckets.containsKey(name)) {
			return buckets.get(name);
		}
		throw new BucketNotFoundException("bucket " + name + " not found !");
	}
	
	public String getBucketByEntity(Class<?> entity){
		String str = entityBuckets.get(entity.getSimpleName().toLowerCase());
		if(str == null || str.trim().length() == 0){
			return this.defaultBucket;
		}
		return str;
	}
	
	public String getBucket(String ddoc){
		String str = entityBuckets.get(ddoc.toLowerCase());
		if(str == null || str.trim().length() == 0){
			return this.defaultBucket;
		}
		return str;
	}
	
	public static String getCKeyByEntity(BucketEntity entity){
		return entity.getCouchbaseKey();
	}
	
	public static String getCKeyByEntity(Class<?> clzz, Long id) throws BucketException{
		if(id == null){
			throw new BucketException("getCKeyByEntity: id is NULL.");
		}
		return String.format("%s:%s", clzz.getSimpleName(), id);
	}
	
	protected void init() throws Exception {
		
		initMetrics();
		
		this.buckets = new HashMap<String, CouchbaseClient>();
		this.entityBuckets = new HashMap<String, String>();
		
		List<Map> items = (List<Map>) this.config.getBuckets();

		CouchbaseConnectionFactoryBuilder builder = new CouchbaseConnectionFactoryBuilder();
		builder.setDaemon(true);
		builder.setEnableMetrics(MetricType.PERFORMANCE);
		builder.setFailureMode(FailureMode.Redistribute);
		
		for (Map item : items) {
			String name = Objects.toString(item.get("bucket"), "").trim();
			String user = Objects.toString(item.get("user"), "");
			String pwd = Objects.toString(item.get("pwd"), "");
			String urls = Objects.toString(item.get("urls"), "");
			String[] ents = Objects.toString(item.get("ents"), "").split(",");
            logger.info("@Couchbase connecting to bucket: {}, urls: {}", name, urls);
			CouchbaseConnectionFactory ccf = null;
            if(StringUtils.isNotBlank(user)){
                ccf = builder.buildCouchbaseConnection(this.wrapBaseList(urls), name, user, pwd);
            }else{
                ccf = builder.buildCouchbaseConnection(this.wrapBaseList(urls), name, pwd);
            }
			this.buckets.put(name, new CouchbaseClient(ccf));
			this.defaultBucket = name;
			for(String str : ents){
				this.entityBuckets.put(str.toLowerCase().trim(), name);
			}
			logger.info("@Couchbase init, bucket:{}, urls:{}", name, urls);
		}
	}

	protected void initMetrics() {
		Map metrics = this.config.getMetrics();
		String reportType = "jmx";
		// net.spy.memcached.metrics.DefaultMetricCollector
		if(metrics!=null){
			if(metrics.get("type") != null){
				reportType = ObjectUtils.toString(metrics.get("type"));
			}
			if(metrics.get("out") != null){
				System.setProperty("net.spy.metrics.reporter.outdir", ObjectUtils.toString(metrics.get("out")));
			}
			if(metrics.get("interval") != null){
				System.setProperty("net.spy.metrics.reporter.interval", ObjectUtils.toString(metrics.get("interval")));
			}
		}
		
		System.setProperty("net.spy.metrics.reporter.type", reportType);
	}
	
	private List<URI> wrapBaseList(String urls){
		List<URI> baseList = new ArrayList<URI>();
		String[] temp = urls.split(",");
		for(String str : temp){
			baseList.add(URI.create(str));
		}
		return baseList;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.init();
		BucketManager.instance = this;
		logger.info("@Couchbase init, DONE");
	}

	/**
	 * Make sure the given object is not a iterable.
	 * 
	 * @param o
	 *            the object to verify.
	 */
	public final static void ensureNotIterable(Object o) {
		if (null != o) {
			if (o.getClass().isArray()
					|| ITERABLE_CLASSES.contains(o.getClass().getName())) {
				throw new IllegalArgumentException(
						"Cannot use a collection here.");
			}
		}
	}

	public String getDefaultBucket() {
		return defaultBucket;
	}

}

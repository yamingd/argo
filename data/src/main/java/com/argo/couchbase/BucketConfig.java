package com.argo.couchbase;

import com.argo.core.configuration.AbstractConfig;

import java.util.List;
import java.util.Map;

/**
 * Couchbase配置读取.
 * 
 * <pre>
 *   /WEB-INF/resources/dev/couchbase.yaml
 *   /WEB-INF/resources/test/couchbase.yaml
 *   /WEB-INF/resources/prod/couchbase.yaml
 * </pre>
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
 * 
 * @author yaming_deng
 * 
 */
public class BucketConfig extends AbstractConfig {

    private static final String confName = "couchbase";

    public static BucketConfig instance = null;

	@Override
	public void afterPropertiesSet() throws Exception {
		cfgFile = confName + ".yaml";
		super.afterPropertiesSet();
        BucketConfig.instance = this;
	}
	
	public Map<?, ?> getMetrics(){
		Object temp = this.getValue("metrics");
		if(temp!=null){
			return (Map)temp;
		}
		return null;
	}
	
	public List<?> getBuckets(){
		Object temp = this.getValue("buckets");
		if(temp!=null){
			return (List)temp;
		}
		return null;
	}

    @Override
    public String getConfName() {
        return confName;
    }
}

package com.argo.couchbase;

import com.couchbase.client.CouchbaseClient;

import java.net.URI;
import java.util.ArrayList;

/**
 * <pre>
 * 在WEB-INF/resources/配置如下
 * 
 * <pre>
 *   /WEB-INF/resources/dev/couchbase.yaml
 *   /WEB-INF/resources/test/couchbase.yaml
 *   /WEB-INF/resources/prod/couchbase.yaml
 * </pre>
 *
 * </pre>
 * 
 * @author yaming_deng
 *
 */
public class UsageSample {

    public static void main(String[] args) {
        ArrayList<URI> nodes = new ArrayList<URI>();

        // Add one or more nodes of your cluster (exchange the IP with yours)
        nodes.add(URI.create("http://192.168.1.104:8091/pools"));

        // Try to connect to the client
        CouchbaseClient client = null;
        try {
            client = new CouchbaseClient(nodes, "default", "");
        } catch (Exception e) {
            System.err.println("Error connecting to Couchbase: " + e.getMessage());
            System.exit(1);
        }

        // Set your first document with a key of "hello" and a value of "couchbase!"

        // Return the result and cast it to string
        String result = (String) client.get("hello");
        System.out.println(result);

        // Shutdown the client
        client.shutdown();
    }

}

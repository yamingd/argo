package com.argo.elasticsearch.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.util.List;
import java.util.Map;

public class ElasticsearchTransportClientFactoryBean extends ElasticsearchAbstractClientFactoryBean {

	protected final Log logger = LogFactory.getLog(getClass());
		
	@Override
	protected Client buildClient() throws Exception {
        ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder();
        
        Map<?, ?> cfg = this.config.getTransport();
        builder.put("client.transport.sniff", (Boolean)cfg.get("sniff"));
        builder.put("client.transport.ping_timeout", (Integer)cfg.get("ping_timeout"));
        builder.put("node.client", (Boolean)cfg.get("client"));
        builder.put("node.data", (Boolean)cfg.get("data"));
        builder.put("cluster.name", (String)cfg.get("cluster"));
        
		TransportClient client = new TransportClient(builder.build());
		
		List nodes = this.config.getNodesConfig();
		for(Object node : nodes){
			client.addTransportAddress(toAddress((String)node));
		}

		return client;
	}
	
	/**
	 * Helper to define an hostname and port with a String like hostname:port
	 * @param address Node address hostname:port (or hostname)
	 * @return
	 */
	private InetSocketTransportAddress toAddress(String address) {
		if (address == null) return null;
		
		String[] splitted = address.split(":");
		int port = 9300;
		if (splitted.length > 1) {
			port = Integer.parseInt(splitted[1]);
		}
		
		return new InetSocketTransportAddress(splitted[0], port);
	}
	
}

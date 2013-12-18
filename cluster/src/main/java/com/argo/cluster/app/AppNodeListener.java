package com.argo.cluster.app;

/**
 * 应用实例变化监听器.
 * @author yaming_deng
 *
 */
public interface AppNodeListener {

	void onAdded(String path, String uri);
	/**
	 * 
	 * @param path
	 * @param uri
	 */
	void onRemoved(String path, String uri);
}

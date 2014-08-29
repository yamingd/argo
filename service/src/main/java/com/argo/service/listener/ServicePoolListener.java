package com.argo.service.listener;

import java.util.List;

/**
 * 远程服务池变化响应.
 * @author yaming_deng
 *
 */
public interface ServicePoolListener {
	/**
	 * service changed events from servers.
	 * @param name 名字, 可以为javaBean名称
	 * @param urls 新的URLS.
	 */
	void onServiceChanged(String name, List<String> urls);

    /**
     * 选择一个可用的服务所在服务器地址.
     * @param name
     * @return
     */
    String select(String name);

}

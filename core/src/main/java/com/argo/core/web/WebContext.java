package com.argo.core.web;

import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 存放Web端来环境变量
 *
 * @author yaming_deng
 * @date 2013-1-16
 */
public class WebContext implements Serializable {
	
	/**
	 * 
	 */
	public static final String WEB_ROOT = "web.root";
	public static final String CLIENT_IP = "client.ip";
	public static final String CLIENT_LANG = "lang";
    public static final String CONTEXT_PATH = "shard";
	/**
	 * 
	 */
	private static final long serialVersionUID = -7787309065037672609L;
	
	static ThreadLocal<WebContext> webContext = new ThreadLocal<WebContext>();
	
	private Map<String, Object> context = null;
	
    public static void setContext(WebContext context) {
    	webContext.set(context);
    }

    public static WebContext getContext() {
    	WebContext ctx = webContext.get();
    	if(ctx == null){
    		ctx = new WebContext();
    		webContext.set(ctx);
    	}
    	return ctx;
    }
    	
	/**
	 * 设置环境数据(Key,Value)
	 * @param key
	 * @param value
	 */
	public void put(String key, String value){
		if(context==null){
			context = new HashMap<String, Object>();
		}
		context.put(key, value);
	}
	
	public Object get(String key){
		return context.get(key);
	}
	
	/**
	 * 获取环境数据
	 * @return
	 */
	public Map<String, Object> getMap(){
		return context;
	}
	
	public String getRootPath(){
		return ObjectUtils.toString(this.get(WEB_ROOT));
	}
	public void setRootPath(String path){
		this.put(WEB_ROOT, path);
	}
	
	public String getRequestIp(){
		return ObjectUtils.toString(this.get(CLIENT_IP));
	}
	public void setRequestIp(String value){
		this.put(CLIENT_IP, value);
	}
	
	public String getLang(){
		return ObjectUtils.toString(this.get(CLIENT_LANG));
	}
	public void setLang(String value){
		this.put(CLIENT_LANG, value);
	}

    public String getContextPath(){
        return ObjectUtils.toString(this.get(CONTEXT_PATH));
    }
}

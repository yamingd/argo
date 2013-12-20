package com.argo.core.configuration;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-6
 * Time: 下午10:19
 */
public class SiteConfig extends AbstractConfig implements ConfigListener {

    public static SiteConfig instance = null;
    public static final String confName = "application";

    @Override
    public void afterPropertiesSet() throws Exception {
        this.cfgFile = confName + ".yaml";
        super.afterPropertiesSet();
        SiteConfig.instance = this;
    }

    public Map getApp(){
        return this.get(Map.class, "app");
    }

    public Map getStatic(){
        return this.get(Map.class, "static");
    }

    public Map getFile(){
        return this.get(Map.class, "file");
    }

    public Map getCookie(){
        return this.get(Map.class, "cookie");
    }

    public Map getMetrics(){
        return this.get(Map.class, "metirics");
    }

    public boolean isCookieSecure() {
        Map ret = getCookie();
        if (ret == null){
            return true;
        }
        return (Boolean)ret.get("secure");
    }

    public String getActionFullUrl(String url){
        Map map = this.getApp();
        if(map == null){
            return null;
        }
        String domain = (String)map.get("domain");
        if(url.startsWith("/")){
            url = url.substring(1);
        }
        return domain + url;
    }

    public String getActionFullUrl(String contextPath, String url){
        return this.getActionFullUrl(contextPath+url);
    }

    @Override
    public String getConfName() {
        return confName;
    }

}

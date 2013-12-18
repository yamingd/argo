package com.argo.cluster;

import com.argo.cluster.app.AppNodeRegister;
import com.argo.cluster.configuration.AppConfigMaster;
import com.argo.cluster.configuration.AppConfigWatcher;
import com.argo.core.ContextConfig;
import com.argo.core.configuration.SiteConfig;
import com.argo.core.listener.AppStartUpListener;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-15
 * Time: 下午1:45
 */
public class ClusterAppStartupListener extends AppStartUpListener {

    protected void initSiteConfig() throws Exception{
        String role = ContextConfig.get("role");
        if ("master".equalsIgnoreCase(role)){
           new AppConfigMaster().afterPropertiesSet();
            new AppNodeRegister().afterPropertiesSet();
        }else{
           new AppNodeRegister().afterPropertiesSet();
           new AppConfigWatcher().afterPropertiesSet();
        }
        //1.读取本地配置
        new SiteConfig().afterPropertiesSet();
    }
}

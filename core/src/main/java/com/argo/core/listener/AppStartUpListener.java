package com.argo.core.listener;

import com.argo.core.ContextConfig;
import com.argo.core.configuration.SiteConfig;
import com.argo.core.metric.MetricCollectorImpl;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * 应用系统启动
 * 在程序开始时，第一个启动
 * context-param -> listener -> filter -> servlet 
 * @author yaming_deng
 */
public class AppStartUpListener implements ServletContextListener {
	

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		try {
		} catch (Exception e) {
			
		}		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String env = sce.getServletContext().getInitParameter("run-env");
        ContextConfig.setRunning(env);
        try {
            ContextConfig.init(sce.getServletContext());
            this.initSiteConfig();
            //init metircs reporter
            new MetricCollectorImpl(SiteConfig.instance.getMetrics()).afterPropertiesSet();
        } catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}		
	}

    protected void initSiteConfig() throws Exception{
        //1.读取本地配置
        new SiteConfig().afterPropertiesSet();
    }
}

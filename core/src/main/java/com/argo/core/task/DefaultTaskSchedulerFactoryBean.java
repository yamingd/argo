package com.argo.core.task;

import com.argo.core.configuration.SiteConfig;
import com.argo.core.service.ServiceConfig;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 描述 ：
 *
 * @author yaming_deng
 * @date 2012-2-15
 */
@Component("taskSchedulerFactory")
public class DefaultTaskSchedulerFactoryBean extends TaskSchedulerFactory {


	public static DefaultTaskSchedulerFactoryBean instance = null;

	
	public boolean isEnabled(){
        Map map = ServiceConfig.instance.getTasks();
        return (Boolean)map.get("enabled");
	}

	public void afterPropertiesSet()throws Exception{
		if(!this.isEnabled()){
			return;
		}
		DefaultTaskSchedulerFactoryBean.instance = this;
		super.afterPropertiesSet();
	}
	
	public void scheduleJob(Job job) throws Exception{
		if(!this.isEnabled()){
			return;
		}

        Map app = SiteConfig.instance.getApp();
        String appName = (String)app.get("name");

		JobDetail jobDetail = JobBuilder.newJob(job.getClass()).withIdentity(job.getName(), appName).build();
		
		CronTriggerImpl trigger = new CronTriggerImpl();
		trigger.setCronExpression(job.getCronExpression());
		trigger.setJobName(job.getName());
		trigger.setJobGroup(appName);
		
		this.getScheduler().scheduleJob(jobDetail, trigger);
	}
}

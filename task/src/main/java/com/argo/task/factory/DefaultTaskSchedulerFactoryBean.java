package com.argo.task.factory;

import com.argo.core.configuration.SiteConfig;
import com.argo.task.Job;
import com.argo.task.TaskConfig;
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

	public void afterPropertiesSet()throws Exception{
		if(!TaskConfig.instance.isEnabled()){
			return;
		}
		DefaultTaskSchedulerFactoryBean.instance = this;
		super.afterPropertiesSet();
	}
	
	public void scheduleJob(Job job) throws Exception{
        if(!TaskConfig.instance.isEnabled()){
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

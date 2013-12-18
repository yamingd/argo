package com.argo.core.task;

import com.argo.core.base.BaseBean;
import com.argo.core.service.ServiceConfig;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

public abstract class AbstractJob extends BaseBean implements Job, BeanNameAware {
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());
		
	@Autowired
	@Qualifier("taskSchedulerFactory")
	private DefaultTaskSchedulerFactoryBean schedulerFactoryBean;
		
	private String name = "";
	private boolean isRunning = false;
	private String cronExpression = "";
	private boolean concurrent = false;
	private Integer concurrentCount = 0;
	
	@Override
	public void execute() {
		if(this.isRunning){
			log.warn(this.getJobName()+":任务正在运行, 系统暂不支持任务并发执行.");
			return;
		}
		this.isRunning = true;
		try{
			
			this.postExecute();
			
		}catch (Exception e) {
			log.error(this.getJobName()+":后台任务出现不可预料的错误.", e);
		}
		this.isRunning = false;
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException{
		this.execute();
	}
	
	private String getJobName() {
		String jobName = this.getClass().getName();
		return jobName;
	}
	
	/**
	 * 执行实际任务
	 */
	public abstract void postExecute() throws Exception;

	public String getName(){
		return this.name;
	}
	
	/* (non-Javadoc)
	 */
	@Override
	public String getCronExpression() {
		if(this.cronExpression==null){
            Map map = ServiceConfig.instance.getTasks();
            Map t = (Map)map.get(this.getName());
            this.cronExpression = (String)t.get("cron");
		}
		return this.cronExpression;
	}

	/* (non-Javadoc)
	 */
	@Override
	public boolean isConcurrent() {
		return this.concurrent;
	}

	/* (non-Javadoc)
	 */
	@Override
	public void setConcurrent(boolean concurrent) {
		this.concurrent = concurrent;		
	}

	/* (non-Javadoc)
	 */
	@Override
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
        Map map = ServiceConfig.instance.getTasks();
        Map t = (Map)map.get(this.getName());
		boolean flag = (Boolean)t.get("enabled");
		if(flag){
			log.info("scheduleJob: " + this.getName());
			this.schedulerFactoryBean.scheduleJob(this);
		}
	}

	/* (non-Javadoc)
	 */
	@Override
	public Integer getConcurrentCount() {
		return this.concurrentCount;
	}

	/* (non-Javadoc)
	 */
	@Override
	public void setConcurrentCount(Integer count) {
		this.concurrentCount = count;		
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
	 */
	@Override
	public void setBeanName(String name) {
		this.name = name;
	}
}

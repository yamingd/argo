package com.argo.task;

import com.argo.core.base.BaseBean;
import com.argo.task.factory.DefaultTaskSchedulerFactoryBean;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

public abstract class AbstractJob extends BaseBean implements Job, BeanNameAware {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
		
	@Autowired
	@Qualifier("taskSchedulerFactory")
	private DefaultTaskSchedulerFactoryBean schedulerFactoryBean;
		
	private String name = "";
	private boolean isRunning = false;
	private String cronExpression = "";
	private boolean concurrent = false;
	private Integer concurrentCount = 0;
	private Map taskProp;

	@Override
	public void execute() {
		try{
			
			this.postExecute();
			
		}catch (Exception e) {
			logger.error(this.getJobName()+":后台任务出现不可预料的错误.", e);
		}
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
            this.cronExpression = (String)taskProp.get("cron");
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
        taskProp = TaskConfig.instance.getTask(this.getName());
		boolean flag = (Boolean)taskProp.get("enabled");
		if(flag){
			logger.info("scheduleJob: " + this.getName());
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

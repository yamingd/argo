package com.argo.task;

import org.springframework.beans.factory.InitializingBean;

/**
 * 代码
 * @Task("abc")
 * public class AbcJob extends AbstractJob{
 * 		...
 * }
 * 配置
 * tasks:
 *   abc:
 *      enabled : true / false
 *      cron : "0 0/30 * * * ?"
 *    
 * 后台任务接口定义
 * @author yaming_deng
 *
 */
public interface Job extends InitializingBean, org.quartz.Job {
	
	void execute();
	
	String getName();
	
	String getCronExpression();
	void setCronExpression(String cronExpression);
	
	boolean isConcurrent();
	void setConcurrent(boolean concurrent);
	
	Integer getConcurrentCount();
	void setConcurrentCount(Integer count);
}

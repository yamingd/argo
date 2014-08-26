package com.argo.db.shard;

import com.argo.core.exception.UserNotAuthorizationException;
import com.argo.core.policy.IdDef;
import com.argo.core.policy.IdGenPolicy;
import com.argo.core.web.session.SessionCookieHolder;
import com.argo.db.beans.IdPolicyFactoryBean;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 
 *  从用户标识里解析出数据库(ShardDbDef)并初始化DatafarmContext
 *  在SaaS和Cookie下使用
 *  并且Cookie里放到用户标识是Long型
 *  
 *  <filter>
		<filter-name>dataShardContextSetFilter</filter-name>
		<filter-class>com.argo.data.db.shard.DataShardContextSetFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>dataShardContextSetFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
 *  
 * @author yaming_deng
 * @date 2013-1-17
 */
public class DataShardContextSetFilter implements Filter {

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
				
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		
		if (!(req instanceof HttpServletRequest)) {
			throw new ServletException("HttpServletRequest required");
		}

		if (!(res instanceof HttpServletResponse)) {
			throw new ServletException("HttpServletResponse required");
		}
		
		HttpServletRequest request = (HttpServletRequest) req;
		
		try {
			String uid = SessionCookieHolder.getCurrentUID(request);
			if(StringUtils.isNotBlank(uid)){
				if(StringUtils.isNumeric(uid)){
					IdGenPolicy bean = IdPolicyFactoryBean.instance.getCurrent();
					if(bean==null){
						throw new ServletException("can't find IdGenPolicy bean.");
					}
					IdDef id = bean.parse(Long.parseLong(uid));
					DataShardContext context = DataShardContext.getContext();
					context.setShardId(id.getShardId());
				}else{
					throw new ServletException("CurrentUID is expected to be Long.");
				}
			}
		} catch (UserNotAuthorizationException e) {
			
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
				
	}

}

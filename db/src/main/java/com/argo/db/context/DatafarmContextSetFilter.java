package com.argo.db.context;

import com.argo.core.exception.UserNotAuthorizationException;
import com.argo.core.web.session.SessionCookieHolder;
import com.argo.db.farm.ServerFarm;
import com.argo.db.farm.ShardIdDef;
import com.argo.db.identity.IdPolicy;
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
		<filter-name>datafarmContextSetFilter</filter-name>
		<filter-class>com.argo.data.db.context.DatafarmContextSetFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>datafarmContextSetFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
 *  
 * @author yaming_deng
 * @date 2013-1-17
 */
public class DatafarmContextSetFilter implements Filter {

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
					IdPolicy bean = ServerFarm.current.getIdPolicy();
					if(bean==null){
						throw new ServletException("can't find IdPolicy bean.");
					}
					ShardIdDef id = bean.parse(Long.parseLong(uid));
					DatafarmContext context = DatafarmContext.getContext();
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

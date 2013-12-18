package com.argo.core.web.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CSRF攻击检查
 *
 * @author yaming_deng
 * @date 2012-5-23
 */
public class CSRFTokenFilter implements Filter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1179085738490906926L;
	
	private FilterConfig config = null;
	private String[] filterUrls = null;
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
				
	}

	protected boolean isAjax(HttpServletRequest request){
    	return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		String method = httpRequest.getMethod();
		if("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)){
			if(!this.isUrlFiltered(httpRequest.getRequestURI())){
				String formToken = httpRequest.getParameter("_csrf_");
				Cookie clientToken = WebUtils.getCookie(httpRequest, "_csrf_");
				if(clientToken==null 
						|| StringUtils.isBlank(formToken) 
						|| !formToken.equals(clientToken.getValue())){
					
					if(this.isAjax(httpRequest)){
						HttpServletResponse resp = (HttpServletResponse)response;
						resp.setStatus(401);
						resp.getWriter().write("{'error':'You are not allowed to execute this action'}");
						resp.setContentType("application/json; charset=UTF-8");
						resp.addHeader("Cache-Control", "no-store");
						resp.addHeader("Pragma", "no-cache");
						resp.getWriter().flush();
						resp.getWriter().close();
						return;
					}else{
						throw new ServletException("You are not allowed to execute this action. _csrf_ checking");
					}
				}
			}
		}
		
		chain.doFilter(request, response);
	}
	
	/**
	 * URL是否需要过滤掉.
	 * @param url
	 * @return
	 */
	private boolean isUrlFiltered(String url){
		if(this.filterUrls==null){
			return false;
		}
		url = url.trim().toLowerCase();
		for(String item : this.filterUrls){
			if(url.startsWith(item.toLowerCase())){
				return true;
			}
		} 
		return false;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.config = filterConfig;	
		if(this.config!=null){
			String value = this.config.getInitParameter("filters");
			filterUrls = StringUtils.split(value, ",");
		}
	}
}

package com.argo.core.web.session;


import com.argo.core.base.BaseUser;
import com.argo.core.exception.UserNotAuthorizationException;

/**
 * 描述 ：当前用户内存存储
 *
 * @author yaming_deng
 * @date 2012-12-17
 */
public class SessionUserHolder {
	
	private static final ThreadLocal<BaseUser> threadLocal;
	static {
		threadLocal = new ThreadLocal<BaseUser>();
	}
	
	/**
	 * 设置当前用户.
	 * @param user
	 */
	public static void set(BaseUser user){
		if(user!=null){
			threadLocal.set(user);
		}
	}
	
	/**
	 * 获取当前用户.
	 * @param <T>
	 * @return
	 * @throws UserNotAuthorizationException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get() throws UserNotAuthorizationException {
		BaseUser user =  threadLocal.get();
		if(user == null){
			throw new UserNotAuthorizationException("SessionUserHolder");
		}
		return (T)user;
	}
	
	/**
	 * 注销当前用户.
	 */
	public static void expire(){
		threadLocal.remove();
	}
}

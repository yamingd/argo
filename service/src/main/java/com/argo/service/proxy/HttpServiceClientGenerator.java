package com.argo.service.proxy;

import com.argo.service.factory.ServiceNameBuilder;
import com.argo.service.listener.ServicePoolListener;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;


@Component("httpServiceClientGenerator")
public class HttpServiceClientGenerator implements ServiceClientGenerator {

    private static final Log logger = LogFactory.getLog(HttpServiceClientGenerator.class);
    public static final String PROTOCOL_HTTP = "http://";

    public static HttpServiceClientGenerator instance = null;

    @Override
    public String getProtocalMark() {
        return PROTOCOL_HTTP;
    }

    @Override
    public String stripServiceName(String val){
        return val.replace(PROTOCOL_HTTP, "");
    }

	/**
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getService(Class<T> clazz) {
		T f = (T) Proxy.newProxyInstance(clazz.getClassLoader(),
				new Class[] { clazz }, new ServiceHandler<T>(clazz, null));
		return f;
	}
	
	/**
	 * @param <T>
	 * @param clazz
	 * @param serviceName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getService(Class<T> clazz, String serviceName) {
		T f = (T) Proxy.newProxyInstance(clazz.getClassLoader(),
				new Class[] { clazz }, new ServiceHandler<T>(clazz, serviceName));
		return f;
	}
	
	/**
	 * 调用远程代理方法的过程.
	 * 
	 * @param <T>
	 */
	public static class ServiceHandler<T> implements InvocationHandler {
		private Class<T> clazz;
		private String serviceName;
		private ServicePoolListener servicePool = null;
		
		public ServiceHandler(Class<T> clazz, String serviceName) {
			this.clazz = clazz;
			this.servicePool = ServiceClientPoolListener.instance;
			this.serviceName = ServiceNameBuilder.get(clazz, serviceName);
		}
		
		private String getAvaliableServerUrl() throws Exception{
			// http://192.168.39.60:7980/remoting/
			String server = servicePool.select(this.serviceName);
			if(server!=null){
				return String.format("http://%s/remoting/%s", server, this.serviceName);
			}
			throw new Exception("@@@Remoting Server DOWN.");
		}
		
		public Object invoke(final Object proxy, Method method, Object[] args)
				throws Exception {
			
			return postInvoke(method, args, 1);
		}

		@SuppressWarnings("unchecked")
		private Object postInvoke(Method method, Object[] args, Integer retry)
				throws Exception {
			
			HttpInvokerProxyFactoryBean bean = genDynamicBean();
			
			try {
				bean.afterPropertiesSet();
				T service = (T) bean.getObject();
				return method.invoke(service, args);
			}catch(RemoteConnectFailureException e){
				if(retry.intValue()==0){
					throw new Exception("远程服务器连接失败:", e);
				}
				logger.error("远程服务器连接失败, 进行重试, 参数: " + ToStringBuilder.reflectionToString(args));
				//失败后重试
				return this.postInvoke(method, args, retry - 1);
			}catch (Exception e) {
				logger.error("远程调用错误:" + ToStringBuilder.reflectionToString(args),e);
				throw new Exception("远程调用错误" + new Date(), e);
			}
		}

		private HttpInvokerProxyFactoryBean genDynamicBean() {
			
			HttpInvokerProxyFactoryBean bean = new HttpInvokerProxyFactoryBean() {
				
				@SuppressWarnings("unchecked")
				@Override
				public Class getServiceInterface() {
					try {
						return Class.forName(clazz.getName());
					} catch (ClassNotFoundException e) {
						logger.error("远程调用错误ClassNotFoundException", e);
					}
					return super.getServiceInterface();
				}

				@Override
				public String getServiceUrl() {
					try {
						return getAvaliableServerUrl();
					} catch (Exception e) {
						this.logger.error("getServiceUrl", e);
						return "Can't Get Remoting ServiceUrl. NULL.";
					}
				}
			};
			return bean;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {		
		HttpServiceClientGenerator.instance = this;
	}
}

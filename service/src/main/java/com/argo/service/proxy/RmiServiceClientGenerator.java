package com.argo.service.proxy;

import com.argo.service.factory.ServiceNameBuilder;
import com.argo.service.listener.ServicePoolListener;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;



@Component("rmiServiceClientGenerator")
public class RmiServiceClientGenerator implements ServiceClientGenerator {

    private static final Log logger = LogFactory.getLog(RmiServiceClientGenerator.class);

    public static final String PROTOCOL_RMI = "rmi://";

    public static RmiServiceClientGenerator instance = null;

    @Override
    public String getProtocalMark() {
        return PROTOCOL_RMI;
    }

    @Override
    public String stripServiceName(String val){
        return val.replace(PROTOCOL_RMI, "");
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
		
		public ServiceHandler(final Class<T> clazz, final String serviceName) {
			this.clazz = clazz;
			this.servicePool = ServiceClientPoolListener.instance;
			this.serviceName = ServiceNameBuilder.get(clazz, serviceName);
		}
		
		private String getAvaliableServerUrl() throws Exception{
			// http://192.168.39.60:7980/remoting/
			String server = servicePool.select(this.serviceName);
			if(server!=null){
				return String.format("rmi://%s/%s", server, this.serviceName);
			}
			throw new Exception("@@@Remoting Server DOWN.");
		}

        public Object invoke(final Object proxy, Method method, Object[] args)
				throws Exception {

            String name = method.getName();
            if ("equals".equalsIgnoreCase(name)){
                Object o = args[0];
                if (o.getClass().isAssignableFrom(this.clazz)){
                    return true;
                }else{
                    return false;
                }
            }

            if ("toString".equalsIgnoreCase(name)){
                return this.clazz.getName() + "/" + this.serviceName;
            }

			return postInvoke(method, args, 1);
		}

		@SuppressWarnings("unchecked")
		private Object postInvoke(Method method, Object[] args, Integer retry)
				throws Exception {
			
			RmiProxyFactoryBean bean = genDynamicBean();
			
			logger.debug("postInvoke:" + bean.getServiceUrl());
			
			try {
				bean.afterPropertiesSet();
				T service = (T) bean.getObject();
				return method.invoke(service, args);
			}catch(RemoteConnectFailureException e){
				if(retry.intValue()==0){
					throw new Exception("远程服务器连接失败:", e);
				}
                Thread.sleep(100);
				return this.postInvoke(method, args, retry - 1);
			}catch(RemoteLookupFailureException e){
				if(retry.intValue()==0 || args == null){
					throw new Exception("远程服务定位失败:", e);
				}
                Thread.sleep(100);
				return this.postInvoke(method, args, retry - 1);
			}
			catch (Exception e) {
                if (args != null) {
                    logger.error("远程调用错误:" + ObjectUtils.toString(args), e);
                }
				throw new Exception("远程调用错误" + new Date(), e);
			}
		}

		private RmiProxyFactoryBean genDynamicBean() {
			
			RmiProxyFactoryBean bean = new RmiProxyFactoryBean() {
				
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
						String url = getAvaliableServerUrl();
                        logger.debug("ServiceUrl: " + url);
                        return url;
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
		RmiServiceClientGenerator.instance = this;
	}
}

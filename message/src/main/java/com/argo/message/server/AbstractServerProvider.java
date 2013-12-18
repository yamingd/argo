package com.argo.message.server;


import com.argo.core.base.BaseBean;

/**
 * 描述 ：
 *
 * @author yaming_deng
 * @date 2012-4-19
 */
public abstract class AbstractServerProvider extends BaseBean implements ServerProvider {
		
	private boolean active = false;
	private boolean running = false;
	protected final Object lifecycleMonitor = new Object();
	private String providerName;

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
        this.initialize();
		this.active = true;
		this.running = true;
	}
	
	/* (non-Javadoc)
	 * @see com.kingdee.jms.server.ServerProvider#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		
		synchronized (this.lifecycleMonitor) {
			this.running = false;
			this.active = false;
			this.lifecycleMonitor.notifyAll();
		}
		
		this.doShutdown();
		
	}
	
	protected abstract void doShutdown();
	
	/**
	 * 看这个类有没给销毁
	 * @return
	 */
	public final boolean isActive() {
		synchronized (this.lifecycleMonitor) {
			return this.active;
		}
	}

	/**
	 * 看这个类有没给销毁
	 * @return
	 */
	public final boolean isRunning() {
		synchronized (this.lifecycleMonitor) {
			return this.running;
		}
	}

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
}

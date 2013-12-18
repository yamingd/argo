package com.argo.core.password;

import com.argo.core.base.BaseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractPasswordService extends BaseBean implements PasswordService {
	
	@Autowired
	@Qualifier("passwordServiceFactory")
	private PasswordServiceFactory passwordServiceFactory;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		this.passwordServiceFactory.addService(this);
	}
}

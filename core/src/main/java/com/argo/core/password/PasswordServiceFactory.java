package com.argo.core.password;

import com.argo.core.base.BaseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("passwordServiceFactory")
public class PasswordServiceFactory {

	@Autowired
	@Qualifier("defaultPasswordService")
	private PasswordService defaultService;

	private List<PasswordService> serviceList = new ArrayList<PasswordService>();
	
	/**
	 * 按照用户的标识来采用不同的密码服务.
	 * @param user
	 * @return
	 */
	public PasswordService find(BaseUser user){
		for (PasswordService item : serviceList) {
			if(item.getModeId().equals(user.getPasswdMode())){
				return item;
			}
		}
		return defaultService;
	}
	
	public void addService(PasswordService item){
		this.serviceList.add(item);
	}

	/**
	 * @return the defaultService
	 */
	public PasswordService getDefaultService() {
		return defaultService;
	}
}

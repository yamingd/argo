package com.argo.core.password;

import com.argo.core.base.BaseUser;
import com.argo.core.utils.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("ucPasswordService")
public class UCPasswordService extends AbstractPasswordService {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public String encrypt(String password, String salt) {
		password = password.toLowerCase();
		password = TokenUtil.md5(password);
		password = TokenUtil.md5(password + salt);
		return password;
	}

	@Override
	public boolean validate(String password, String email, BaseUser user) {
		String[] epwd = user.getHashPasswd().split(",");
		if(epwd.length<=1){
			return false;
		}
		String encrypt_passwd = this.encrypt(password, epwd[0]);
		if(logger.isDebugEnabled()){
			this.logger.debug("validate MD5 Password, dp=" + user.getHashPasswd() + ", up=" + encrypt_passwd);
		}
		return epwd[1].equalsIgnoreCase(encrypt_passwd);
	}

	@Override
	public Integer getModeId() {
		return 3;
	}
}

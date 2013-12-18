package com.argo.core.password;

import com.argo.core.base.BaseUser;
import com.argo.core.utils.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("md5PasswordService")
public class Md5PasswordService extends AbstractPasswordService {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public String encrypt(String password, String email) {
		password = TokenUtil.md5(password);
		return password;
	}

	@Override
	public boolean validate(String password, String email, BaseUser user) {
		String encrypt_passwd = this.encrypt(password, email);
		if(logger.isDebugEnabled()){
			this.logger.debug("validate MD5 Password, dp=" + user.getHashPasswd() + ", up=" + encrypt_passwd);
		}
		return user.getHashPasswd().equalsIgnoreCase(encrypt_passwd);
	}

	@Override
	public Integer getModeId() {
		return 2;
	}
}

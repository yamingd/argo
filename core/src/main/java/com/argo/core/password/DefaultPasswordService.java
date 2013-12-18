package com.argo.core.password;

import com.argo.core.base.BaseUser;
import com.argo.core.utils.TokenUtil;
import org.springframework.stereotype.Service;


@Service("defaultPasswordService")
public class DefaultPasswordService extends AbstractPasswordService {

	public static Integer NS_MODE_ID = 1;
	
	@Override
	public String encrypt(String password, String email) {
		email = email.trim().toLowerCase();
		password = TokenUtil.generate(password, email + getPasswordSecretSalt());
		return password;
	}

	/**
	 * @return
	 */
	private String getPasswordSecretSalt() {
		return TokenUtil.md5((String)this.getSiteConfig().getApp().get("secret"));
	}

	@Override
	public boolean validate(String password, String email, BaseUser user) {
		String encrypt_passwd = this.encrypt(password, email);
		return user.getHashPasswd().equals(encrypt_passwd);
	}

	@Override
	public Integer getModeId() {
		return NS_MODE_ID;
	}

}

package com.yile.learning.oauth.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import com.rabbitframework.security.SecurityUser;
import com.rabbitframework.security.realm.SecurityAuthorizingRealm;
import com.yile.learning.oauth.biz.UserBiz;
import com.yile.learning.oauth.model.OauthUser;

public class SecurityRealm extends SecurityAuthorizingRealm {
	private static final Logger logger = LogManager.getLogger(SecurityRealm.class);
	@Autowired
	private UserBiz userBiz;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		logger.info("doGetAuthorizationInfo:" + getName());
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		return authorizationInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		logger.debug("AuthenticationInfo:" + getName());
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
		String password = new String(usernamePasswordToken.getPassword());
		password = new Md5Hash(password).toString();
		String username = usernamePasswordToken.getUsername();
		logger.debug("====================doGetAuthenticationInfo begin ==========================");
		logger.debug("username: " + username);
		logger.debug("password: " + password);
		logger.debug("principal: " + usernamePasswordToken.getPrincipal());
		logger.debug("======================doGetAuthenticationInfo end ========================");
		OauthUser oauthUser = userBiz.findByParams(username, password);
		if (oauthUser == null) {
			throw new IncorrectCredentialsException();// 用户名或密码不匹配
		}
		usernamePasswordToken.setPassword(password.toCharArray());
		SecurityUser securityUser = new SecurityUser(oauthUser.getUserId() + "", username);
		securityUser.setUserName(oauthUser.getUserName());
		// ByteSource byteSource = ByteSource.Util.bytes(oauthUser.getSalt());
		return new SimpleAuthenticationInfo(securityUser, password, getName());
	}

}

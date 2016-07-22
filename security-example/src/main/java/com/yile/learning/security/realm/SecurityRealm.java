package com.yile.learning.security.realm;

import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitframework.security.SecurityUser;
import com.rabbitframework.security.realm.SecurityAuthorizingRealm;
import com.yile.learning.biz.UserManagerBiz;
import com.yile.learning.model.UserInfo;

public class SecurityRealm extends SecurityAuthorizingRealm {
	@Autowired
	private UserManagerBiz userManagerBiz;
	private static final Logger logger = LoggerFactory.getLogger(SecurityRealm.class);

	/**
	 * 授权认证,在配有缓存时只调用一次
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		logger.debug("AuthorizationInfo:" + getName());
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		Object obj = principals.getPrimaryPrincipal();
		if (obj instanceof SecurityUser) {
			logger.debug("====================doGetAuthorizationInfo begin ==========================");
			SecurityUser securityUser = (SecurityUser) obj;
			int userId = Integer.parseInt(securityUser.getUserId());
			Set<String> userRoles = userManagerBiz.findUserRoleCodeByUserId(userId);
			authorizationInfo.addRoles(userRoles);
			logger.debug("====================doGetAuthorizationInfo end ==========================");
		}
		String permission1 = "/index";
		String permission2 = "/index/2";
		authorizationInfo.addStringPermission(permission1);
		authorizationInfo.addStringPermission(permission2);
		return authorizationInfo;
	}

	/**
	 * 登录认证,登录时调用
	 */
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
		UserInfo userInfo = userManagerBiz.getUserInfoByLoginName(username);
		if (userInfo == null) {
			throw new UnknownAccountException();// 没有找到帐号
		}
		userInfo = userManagerBiz.getUserInfoByParams(username, password);
		if (userInfo == null) {
			throw new IncorrectCredentialsException();// 用户名或密码不匹配
		}
		usernamePasswordToken.setPassword(password.toCharArray());
		SecurityUser securityUser = new SecurityUser(userInfo.getUserId() + "", username);
		securityUser.setUserName(userInfo.getUserName());
		return new SimpleAuthenticationInfo(securityUser, password, getName());
	}

}

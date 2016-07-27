package com.yile.learning.oauth.service;

import com.yile.learning.oauth.model.OauthUser;

public interface UserService {
	/**
	 * 根据用户名查找用户
	 * 
	 * @param userName
	 * @return
	 */
	public OauthUser findByUserName(String userName, String password);
}

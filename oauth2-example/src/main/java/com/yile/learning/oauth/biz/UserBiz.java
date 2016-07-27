package com.yile.learning.oauth.biz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yile.learning.oauth.model.OauthUser;
import com.yile.learning.oauth.service.UserService;

@Component
public class UserBiz {
	@Autowired
	private UserService userService;

	public OauthUser findByParams(String userName, String password) {
		return userService.findByUserName(userName, password);
	}
}

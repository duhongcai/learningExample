package com.yile.learning.oauth.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.yile.learning.oauth.mapper.UserMapper;
import com.yile.learning.oauth.model.OauthUser;
import com.yile.learning.oauth.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Resource
	private UserMapper userMapper;

	@Override
	public OauthUser findByUserName(String userName, String password) {
		return userMapper.findByUserName(userName, password);
	}

}

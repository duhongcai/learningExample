package com.yile.learning.rest.test.user;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.yile.learning.model.UserInfo;
import com.yile.learning.rest.test.AbstractSpringTestCase;
import com.yile.learning.service.UserService;

public class UserServiceTest extends AbstractSpringTestCase {
	private static final Logger logger = LogManager.getLogger(UserServiceTest.class);
	@Resource
	private UserService userService;

//	@Test
	public void getUserInfo() {
		UserInfo userInfo = userService.getUserInfoByParams("test", "test");
		logger.info("name:" + userInfo.getUserName() + "," + userInfo.getUserPwd());
	}
	@Test
	public void insertUserInfo() {
		userService.insertUserInfo();
	}
}

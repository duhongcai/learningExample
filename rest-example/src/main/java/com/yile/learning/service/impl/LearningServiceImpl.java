package com.yile.learning.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.yile.learning.service.LearningService;

@Component
public class LearningServiceImpl implements LearningService {
	private static final Logger logger = LogManager.getLogger(LearningServiceImpl.class);

	public String getUserName() {
		logger.info("LearningServiceImpl:getUserName");
		return "justin";
	}

}

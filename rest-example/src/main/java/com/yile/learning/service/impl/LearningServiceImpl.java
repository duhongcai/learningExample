package com.yile.learning.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.yile.learning.service.LearningService;

@Service
public class LearningServiceImpl implements LearningService {
	private static final Logger logger = LogManager.getLogger(LearningServiceImpl.class);

	public String getUserName() {
		logger.info("LearningServiceImpl:getUserName");
		return "justin";
	}

}

package com.tjzq.learning.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class AbstractTestCase {
	private static final Logger logger = LogManager.getLogger(AbstractTestCase.class);
	private static final String HOST = "192.168.2.113";
	private static final String USERNAME = "admin";
	private static final String PWD = "admin";
	private static final int PORT = 5672;
	protected ConnectionFactory connectionFactory;
	protected Connection connection;

	@Before
	public void before() {
		connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(HOST);
		connectionFactory.setPort(PORT);
		connectionFactory.setUsername(USERNAME);
		connectionFactory.setPassword(PWD);
		try {
			connection = connectionFactory.newConnection();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (TimeoutException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@After
	public void after() {
		try {
			connection.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
}

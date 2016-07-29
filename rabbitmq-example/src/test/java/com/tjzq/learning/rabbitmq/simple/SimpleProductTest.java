package com.tjzq.learning.rabbitmq.simple;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.tjzq.learning.rabbitmq.AbstractTestCase;

/**
 * 简单的发送与接收
 * 
 * @author justin.liang
 */
public class SimpleProductTest extends AbstractTestCase {
	private Logger logger = LogManager.getLogger(SimpleProductTest.class);
	public final static String QUEUE_NAME = "simple";

	@Test
	public void product() {
		Channel channel = null;
		try {
			channel = connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			String message = "hello world";
			channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
			logger.info("messsage:" + message);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (channel != null) {
				try {
					channel.close();
				} catch (Exception e) {
				}
			}
		}
	}
}

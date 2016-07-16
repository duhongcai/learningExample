package com.tjzq.learning.rabbitmq.publishSubscribe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.tjzq.learning.rabbitmq.AbstractTestCase;

/**
 * 发布、订阅模式，发送端发送广播消息，多个接收端接收。 direct, topic, headers and fanout
 * <p/>
 * fanout:广播消息
 * 
 * @author justin.liang
 */
public class FanoutProductTest extends AbstractTestCase {
	private static final Logger logger = LogManager.getLogger(FanoutProductTest.class);
	public static final String EXCHANGE_NAME = "logs";

	@Test
	public void product() {
		Channel channel = null;
		try {
			channel = connection.createChannel();
			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
			String message = "info: Hello World!";
			channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
			logger.info("sent: '" + message + "'");
		} catch (Exception e) {
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

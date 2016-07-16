package com.tjzq.learning.rabbitmq.singleSendMultiRec;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.tjzq.learning.rabbitmq.AbstractTestCase;

/**
 * 一个发送端，多个接收端，如分布式的任务派发。为了保证消息发送的可靠性， 
 * 不丢失消息，使消息持久化了。同时为了防止接收端在处理消息时down掉，
 * 只有在消息处理完成后才发送ack消息
 * 
 * @author justin.liang
 */
public class ProductTest extends AbstractTestCase {
	private Logger logger = LogManager.getLogger(ProductTest.class);
	public final static String QUEUE_NAME = "task_queue";

	@Test
	public void product() {
		Channel channel = null;
		try {
			channel = connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, true, false, false, null);
			String message = "hello world";
			channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
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

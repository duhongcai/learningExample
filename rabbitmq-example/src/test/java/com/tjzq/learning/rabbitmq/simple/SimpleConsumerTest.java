package com.tjzq.learning.rabbitmq.simple;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.tjzq.learning.rabbitmq.AbstractTestCase;

public class SimpleConsumerTest extends AbstractTestCase {
	private Logger logger = LogManager.getLogger(SimpleConsumerTest.class);

	@Test
	public void consumer() {
		Channel channel = null;
		try {
			channel = connection.createChannel();
			channel.queueDeclare(SimpleProductTest.QUEUE_NAME, false, false, false, null);
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(SimpleProductTest.QUEUE_NAME, true, consumer);
			while (true) {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
				logger.info(" [x] Received: '" + message + "'");
			}
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

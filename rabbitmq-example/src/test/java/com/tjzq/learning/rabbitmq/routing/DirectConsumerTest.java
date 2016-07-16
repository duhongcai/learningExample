package com.tjzq.learning.rabbitmq.routing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.tjzq.learning.rabbitmq.AbstractTestCase;

public class DirectConsumerTest extends AbstractTestCase {
	private Logger logger = LogManager.getLogger(DirectConsumerTest.class);

	@Test
	public void consumer() {
		Channel channel = null;
		try {
			channel = connection.createChannel();
			channel.exchangeDeclare(DirectProductTest.EXCHANGE_NAME, "direct");
			String queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, DirectProductTest.EXCHANGE_NAME, "info");
			channel.queueBind(queueName, DirectProductTest.EXCHANGE_NAME, "debug");

			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueName, true, consumer);
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

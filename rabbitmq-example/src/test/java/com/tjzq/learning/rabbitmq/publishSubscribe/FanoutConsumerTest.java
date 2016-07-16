package com.tjzq.learning.rabbitmq.publishSubscribe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.tjzq.learning.rabbitmq.AbstractTestCase;

public class FanoutConsumerTest extends AbstractTestCase {
	private Logger logger = LogManager.getLogger(FanoutConsumerTest.class);

	@Test
	public void consumer() {
		Channel channel = null;
		try {
			channel = connection.createChannel();
			channel.exchangeDeclare(FanoutProductTest.EXCHANGE_NAME, "fanout");
			String queueName = channel.queueDeclare().getQueue();
			logger.info("queueName:" + queueName);
			channel.queueBind(queueName, FanoutProductTest.EXCHANGE_NAME, "");

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

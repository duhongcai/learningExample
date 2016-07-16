package com.tjzq.learning.rabbitmq.topic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.tjzq.learning.rabbitmq.AbstractTestCase;

public class TopicConsumerTest extends AbstractTestCase {
	private Logger logger = LogManager.getLogger(TopicConsumerTest.class);

	@Test
	public void consumer() {
		Channel channel = null;
		try {
			channel = connection.createChannel();
			channel.exchangeDeclare(TopicProductTest.EXCHANGE_NAME, "topic");
			String queueName = channel.queueDeclare().getQueue();
//			channel.queueBind(queueName, TopicProductTest.EXCHANGE_NAME, "*.info.#");
			channel.queueBind(queueName, TopicProductTest.EXCHANGE_NAME, "debug");
			channel.queueBind(queueName, TopicProductTest.EXCHANGE_NAME, "anonymous.info");
			

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

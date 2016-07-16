package com.tjzq.learning.rabbitmq.singleSendMultiRec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.tjzq.learning.rabbitmq.AbstractTestCase;

public class ConsumerTest extends AbstractTestCase {
	private Logger logger = LogManager.getLogger(ConsumerTest.class);

	@Test
	public void consumer() {
		Channel channel = null;
		try {
			channel = connection.createChannel();
			channel.queueDeclare(ProductTest.QUEUE_NAME, true, false, false, null);
			channel.basicQos(1);
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(ProductTest.QUEUE_NAME, false, consumer);
			while (true) {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
				logger.info(" [x] Received: '" + message + "'");
				doWork(message);
				logger.info("[x] Received: done");
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
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

	private static void doWork(String task) throws InterruptedException {
		for (char ch : task.toCharArray()) {
			if (ch == '.')
				Thread.sleep(1000);
		}
	}
}

package com.nonamedev.marketing.vk.processor.executers;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class GroupMembers extends DefaultConsumer {
	private static final Logger logger = LogManager.getLogger(GroupMembers.class);

	public GroupMembers(Channel channel) {
		super(channel);
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
		String message = new String(body, "UTF-8");
		logger.trace(message);

		try {
			//SocketServerHandler.processNotification(message);
		} catch (Throwable e) {
			logger.catching(e);
		} finally {
			getChannel().basicAck(envelope.getDeliveryTag(), false);
		}
	}

}

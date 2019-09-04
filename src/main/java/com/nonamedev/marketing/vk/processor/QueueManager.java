package com.nonamedev.marketing.vk.processor;

import java.io.IOException;
import java.net.SocketException;
import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nonamedev.marketing.vk.processor.executers.GroupMembers;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class QueueManager {

	private static final int CONNECT_ERRORS_MAX = 3;
	private static final Logger logger = LogManager.getLogger(QueueManager.class);

	private static String rabbitQueue;
	private static Channel rabbitChannel;

	public static void init() {
		initRabbit();
	}

	public static void initRabbit() {
		int errorCount = 0;

		while (errorCount < CONNECT_ERRORS_MAX) {
			try {
				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost(MainApp.Settings.getRabbitHost());
				factory.setAutomaticRecoveryEnabled(true);
				Connection connection = factory.newConnection();
				rabbitQueue = MainApp.Settings.getRabbitQueuePrefix() + "groupMembers";
				rabbitChannel = connection.createChannel();
				rabbitChannel.queueDeclare(rabbitQueue, true, false, false, null);
				rabbitChannel.basicQos(4);
				logger.trace("Rabbit queue starting...");
				rabbitChannel.basicConsume(rabbitQueue, false, new GroupMembers(rabbitChannel));
				logger.trace("Rabbit queue done");
				return;
			} catch (SocketException e) {
				logger.error(MessageFormat.format("Error on init rabbit-connection [attempt #{0}] : {1}", errorCount,
						e.getMessage()));
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
				}
				errorCount++;
			} catch (Throwable e) {
				logger.fatal("Error on init rabbit-connection", e);
				throw new RuntimeException(e);
			}
		}

		if (errorCount >= CONNECT_ERRORS_MAX) {
			throw new RuntimeException(
					MessageFormat.format("Error on init rabbit-connection after {0} attempts", CONNECT_ERRORS_MAX));
		}
	}

	public static void send(String msg) throws IOException {
		rabbitChannel.basicPublish("", rabbitQueue, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
	}
}

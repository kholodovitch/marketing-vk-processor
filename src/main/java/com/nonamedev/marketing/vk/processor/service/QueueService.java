package com.nonamedev.marketing.vk.processor.service;

import java.io.IOException;
import java.net.SocketException;
import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.nonamedev.marketing.vk.processor.MainApp;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;

public abstract class QueueService<T> extends DefaultConsumer {

	private final int CONNECT_ERRORS_MAX = 3;
	private final Logger logger;

	private String rabbitQueue;
	private Channel rabbitChannel;
	private Class<T> typeParameterClass;

	public QueueService(Class<T> typeParameterClass, Channel channel) {
		super(channel);
		this.typeParameterClass = typeParameterClass;
		this.logger = LogManager.getLogger(typeParameterClass);
		initRabbit();
	}

	public void initRabbit() {
		int errorCount = 0;

		while (errorCount < CONNECT_ERRORS_MAX) {
			try {
				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost(MainApp.Settings.getRabbitHost());
				factory.setAutomaticRecoveryEnabled(true);
				Connection connection = factory.newConnection();
				rabbitQueue = MainApp.Settings.getRabbitQueuePrefix() + getQueueName();
				rabbitChannel = connection.createChannel();
				rabbitChannel.queueDeclare(rabbitQueue, true, false, false, null);
				rabbitChannel.basicQos(4);
				logger.trace("Rabbit queue starting...");
				rabbitChannel.basicConsume(rabbitQueue, false, getExecutor(rabbitChannel));
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

	public void send(String msg) throws IOException {
		rabbitChannel.basicPublish("", rabbitQueue, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws IOException {
		String message = new String(body, "UTF-8");
		logger.trace(message);

		try {
			processTask(new Gson().fromJson(message, typeParameterClass));
		} catch (Throwable e) {
			logger.catching(e);
		} finally {
			getChannel().basicAck(envelope.getDeliveryTag(), false);
		}
	}

	protected abstract void processTask(T message);

	protected abstract Consumer getExecutor(Channel rabbitChannel2);

	protected abstract String getQueueName();
}
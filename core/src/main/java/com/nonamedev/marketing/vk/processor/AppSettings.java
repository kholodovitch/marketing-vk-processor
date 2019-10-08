package com.nonamedev.marketing.vk.processor;

public class AppSettings {
	private boolean rabbitActive;
	private String rabbitHost;
	private String rabbitQueuePrefix;

	public boolean getRabbitActive() {
		return rabbitActive;
	}

	public void setRabbitActive(boolean rabbitActive) {
		this.rabbitActive = rabbitActive;
	}

	public String getRabbitHost() {
		return rabbitHost;
	}

	public void setRabbitHost(String rabbitHost) {
		this.rabbitHost = rabbitHost;
	}

	public String getRabbitQueuePrefix() {
		return rabbitQueuePrefix;
	}

	public void setRabbitQueuePrefix(String rabbitQueuePrefix) {
		this.rabbitQueuePrefix = rabbitQueuePrefix;
	}

}

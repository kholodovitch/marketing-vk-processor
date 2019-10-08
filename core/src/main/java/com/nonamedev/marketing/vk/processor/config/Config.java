package com.nonamedev.marketing.vk.processor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class Config {

	private final RammitConfig rabbit = new RammitConfig();

	@Data
	public static class RammitConfig {

		private boolean active;
		private String host;
		private String queuePrefix;

	}

}

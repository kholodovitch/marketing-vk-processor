package com.nonamedev.marketing.vk.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppSettingsManager {
	private static final String CONFIG_DIR = "/etc/kholodovitch/marketing-vk/config/";
	private static final String CONFIG_FILE = "processor.properties";

	private static final Logger logger = LogManager.getLogger(AppSettingsManager.class);

	static void loadConfig() {
		Properties prop = new Properties();

		try {
			loadDefaultConfig(prop);

			File file = new File(getAppConfigPath());
			if (file.exists()) {
				FileInputStream inputStream = new FileInputStream(file);
				try {
					loadConfig(prop, inputStream);
				} finally {
					inputStream.close();
				}
			}
		} catch (IOException e) {
			logger.error("Error on loading config", e);
		} finally {
			parseProperties(prop);
		}
	}

	private static void loadDefaultConfig(Properties prop) {
		String propFileName = "config.properties";
		InputStream inputStream = App.class.getClassLoader().getResourceAsStream(propFileName);
		if (inputStream == null) {
			logger.error("property file '" + propFileName + "' not found in the classpath");
			return;
		}

		loadConfig(prop, inputStream);
	}

	private static void loadConfig(Properties prop, InputStream inputStream) {
		try {
			prop.load(inputStream);
		} catch (IOException e) {
			logger.error("Error on loading config", e);
		}
	}

	private static void parseProperties(Properties prop) {
		AppSettings settings = App.Settings;

		settings.setDbJdbcConnectionString(prop.getProperty("db.jdbc.connectionString"));

		settings.setRabbitActive(Boolean.parseBoolean(prop.getProperty("rabbit.active")));
		settings.setRabbitHost(prop.getProperty("rabbit.host"));
		settings.setRabbitQueuePrefix(prop.getProperty("rabbit.queue.prefix"));
	}

	private static String getAppConfigPath() {
		return CONFIG_DIR + CONFIG_FILE;
	}

}

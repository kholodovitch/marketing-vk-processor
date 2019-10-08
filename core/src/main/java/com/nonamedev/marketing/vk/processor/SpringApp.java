package com.nonamedev.marketing.vk.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class SpringApp {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SpringApp.class, args);
	}

}

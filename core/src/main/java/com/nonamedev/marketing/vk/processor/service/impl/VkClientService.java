package com.nonamedev.marketing.vk.processor.service.impl;

import org.springframework.stereotype.Service;

import com.vk.api.sdk.actions.Groups;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;

@Service
public class VkClientService {
	private final VkApiClient vk;

	public VkClientService() {
		TransportClient transportClient = HttpTransportClient.getInstance();
		vk = new VkApiClient(transportClient);
	}

	public Groups groups() {
		return vk.groups();
	}

}

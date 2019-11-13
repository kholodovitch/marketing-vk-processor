package com.nonamedev.marketing.vk.processor.service.impl;

import org.springframework.stereotype.Service;

import com.vk.api.sdk.actions.Groups;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;

@Service
public class VkClientService {
	private final ServiceActor SERVICE_ACTOR = new ServiceActor(5374209, "aaff4d61aaff4d61aa467e5f32aaad4c60aaaffaaff4d61f24f0d6070dc421ee47c7010");
	
	private final VkApiClient vk;

	public VkClientService() {
		TransportClient transportClient = HttpTransportClient.getInstance();
		vk = new VkApiClient(transportClient);
	}

	public Groups groups() {
		return vk.groups();
	}

	public ServiceActor getServiceActor() {
		return SERVICE_ACTOR;
	}

}

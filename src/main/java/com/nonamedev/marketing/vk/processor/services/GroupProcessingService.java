package com.nonamedev.marketing.vk.processor.services;

import org.springframework.stereotype.Service;

import com.nonamedev.marketing.vk.processor.service.QueueService;
import com.nonamedev.marketing.vk.processor.tasks.GroupTask;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;

@Service
public class GroupProcessingService extends QueueService<GroupTask> {

	public GroupProcessingService(Channel channel) {
		super(GroupTask.class, channel);
	}

	@Override
	protected Consumer getExecutor(Channel channel) {
		return new GroupProcessingService(channel);
	}

	@Override
	protected String getQueueName() {
		return "groupMembers";
	}

	@Override
	protected void processTask(GroupTask message) {
		//
	}

}

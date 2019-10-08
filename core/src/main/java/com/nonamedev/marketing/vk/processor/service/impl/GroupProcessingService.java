package com.nonamedev.marketing.vk.processor.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.nonamedev.marketing.vk.processor.config.Config;
import com.nonamedev.marketing.vk.processor.tasks.GroupTask;
import com.nonamedev.marketing.vk.processor.tasks.UserTask;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.objects.groups.GroupFull;
import com.vk.api.sdk.objects.groups.UserXtrRole;
import com.vk.api.sdk.queries.groups.GroupField;
import com.vk.api.sdk.queries.groups.GroupsGetMembersQueryWithFields;
import com.vk.api.sdk.queries.users.UserField;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GroupProcessingService extends QueueService<GroupTask> {

	private final UserProcessingService userProcessingService;
	private final VkClientService vk;

	public GroupProcessingService(UserProcessingService userProcessingService, VkClientService vk, Config config) {
		super(config);

		this.userProcessingService = userProcessingService;
		this.vk = vk;
	}

	@Override
	protected Consumer getExecutor(Channel rabbitChannel2) {
		return new GroupProcessingService.Executor(GroupTask.class, rabbitChannel2);
	}

	@Override
	protected String getQueueName() {
		return "groupMembers";
	}

	@Override
	protected void processTask(GroupTask group) throws Exception {
		ServiceActor serviceAction = getServiceActor();
		List<GroupFull> vkGroups = vk
				.groups()
				.getById(serviceAction)
				.groupId(Long.toString(group.getSnId()))
				.fields(GroupField.MEMBERS_COUNT)
				.execute();
		if (vkGroups.size() == 0)
			return;

		List<UserXtrRole> vkMembers = IntStream
				.rangeClosed(0, vkGroups.get(0).getMembersCount() / 1000)
				.parallel()
				.mapToObj(x -> getGroupUsers(group.getSnId(), x))
				.flatMap(List::stream)
				.collect(Collectors.toList());

		vkMembers
				.parallelStream()
				.forEach(vkUser -> {
					processUser(group.getSnId(), vkUser);
				});
	}

	private void processUser(long groupId, UserXtrRole vkUser) {
		try {
			UserTask userTask = UserTask
					.builder()
					.snUser(vkUser)
					.groupId(groupId)
					.build();
			userProcessingService.send(userTask);
		} catch (Exception e) {
			log.error(MessageFormat.format("Error on process user ({0}): {1}", vkUser.getId(), e.getMessage()));
		}
	}

	public List<UserXtrRole> getGroupUsers(long groupSnId, int usersPageIndex) {
		try {
			UserField[] fields = new UserField[] { UserField.SEX, UserField.BDATE, UserField.RELATION, UserField.PHOTO_50, UserField.COUNTRY, UserField.CITY,
					UserField.CAN_WRITE_PRIVATE_MESSAGE, UserField.CAN_SEND_FRIEND_REQUEST };

			GroupsGetMembersQueryWithFields request = vk
					.groups()
					.getMembers(getServiceActor(), fields)
					.groupId(Long.toString(groupSnId))
					.offset(usersPageIndex * 1000);
			return request.execute().getItems();
		} catch (Exception e) {
			String exFormat = "Error on request members from VK ({0} : {1}-{2}): {3}";
			String exMsg = MessageFormat.format(exFormat, groupSnId, (usersPageIndex * 1000), ((usersPageIndex + 1) * 1000), e.getMessage());
			log.error(exMsg);
			return new ArrayList<UserXtrRole>();
		}
	}

	private ServiceActor getServiceActor() {
		return new ServiceActor(5374209, "aaff4d61aaff4d61aa467e5f32aaad4c60aaaffaaff4d61f24f0d6070dc421ee47c7010");
	}

	public class Executor extends QueueService.Executor<GroupTask> {

		public Executor(Class<GroupTask> typeParameterClass, Channel channel) {
			super(typeParameterClass, GroupProcessingService.this, channel);
		}

	}

}

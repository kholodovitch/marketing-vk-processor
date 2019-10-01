package com.nonamedev.marketing.vk.processor.services;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.nonamedev.marketing.vk.processor.datalayer.Member;
import com.nonamedev.marketing.vk.processor.repository.MemberRepository;
import com.nonamedev.marketing.vk.processor.service.QueueService;
import com.nonamedev.marketing.vk.processor.tasks.GroupTask;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.GroupFull;
import com.vk.api.sdk.objects.groups.UserXtrRole;
import com.vk.api.sdk.queries.groups.GroupField;
import com.vk.api.sdk.queries.groups.GroupsGetMembersQueryWithFields;
import com.vk.api.sdk.queries.users.UserField;

@Service
public class GroupProcessingService extends QueueService<GroupTask> {

	private final MemberRepository memberRepo;
    private final VkApiClient vk;
	private final Logger logger;

	public GroupProcessingService(MemberRepository memberRepo) {
		super(GroupTask.class);

		this.memberRepo = memberRepo;
		TransportClient transportClient = HttpTransportClient.getInstance();
		vk = new VkApiClient(transportClient);
		this.logger = LogManager.getLogger(GroupProcessingService.class);
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
		ServiceActor seerviceAction = new ServiceActor(5374209, "aaff4d61aaff4d61aa467e5f32aaad4c60aaaffaaff4d61f24f0d6070dc421ee47c7010");
		List<GroupFull> vkGroups = vk.groups().getById(seerviceAction).groupId(Long.toString(group.getSnId())).fields(GroupField.MEMBERS_COUNT).execute();
		if (vkGroups.size() == 0)
			return;

		List<Member> existsMembers = memberRepo.findAllByMemberIdGroupId(group.getId());
		UserField[] fields = new UserField[] { UserField.SEX, UserField.BDATE, UserField.RELATION, UserField.PHOTO_50, UserField.COUNTRY, UserField.CITY, UserField.CAN_WRITE_PRIVATE_MESSAGE, UserField.CAN_SEND_FRIEND_REQUEST };
		IntFunction<List<UserXtrRole>> mapper = new IntFunction<List<UserXtrRole>>() {
			@Override
			public List<UserXtrRole> apply(int value) {
				try {
					GroupsGetMembersQueryWithFields request = vk.groups().getMembers(seerviceAction, fields).groupId(Long.toString(group.getSnId())).offset(value * 1000);
					return request.execute().getItems();
				} catch (Exception e) {
					logger.error(MessageFormat.format("Error on request members from VK ({0} : {1}-{2}): {3}", group.getSnId(), (value * 1000), ((value + 1) * 1000), e.getMessage()));
					return new ArrayList<UserXtrRole>();
				}
			}
		};

		/*
		Consumer<UserXtrRole> action = new Consumer<UserXtrRole>() {
			@Override
			public void accept(UserXtrRole vkUser) {
				try {
					processUser(vkUser, group, existsMembers);
				} catch (Exception e) {
					logger.error(MessageFormat.format("Error on process user ({0}): {1}", vkUser.getId(), e.getMessage()));
				}
			}
		};
		*/

		List<UserXtrRole> vkMembers = IntStream
			.rangeClosed(0, vkGroups.get(0).getMembersCount() / 1000)
			.parallel()
			.mapToObj(mapper)
			.flatMap(List::stream)
			.collect(Collectors.toList());

		//vkMembers.parallelStream().forEach(action);
	}

    public class Executor extends QueueService.Executor<GroupTask> {

		public Executor(Class<GroupTask> typeParameterClass, Channel channel) {
			super(typeParameterClass, GroupProcessingService.this, channel);
		}
	
	}

}

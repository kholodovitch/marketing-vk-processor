package com.nonamedev.marketing.vk.processor.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.nonamedev.marketing.vk.processor.datalayer.Member;
import com.nonamedev.marketing.vk.processor.datalayer.MemberIdentity;
import com.nonamedev.marketing.vk.processor.datalayer.User;
import com.nonamedev.marketing.vk.processor.repository.MemberRepository;
import com.nonamedev.marketing.vk.processor.repository.UserRepository;
import com.nonamedev.marketing.vk.processor.service.QueueService;
import com.nonamedev.marketing.vk.processor.tasks.UserTask;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.UserXtrRole;

@Service
public class UserProcessingService extends QueueService<UserTask> {

	private final MemberRepository memberRepo;
	private final UserRepository userRepo;
	private final VkApiClient vk;
	private final Logger logger;

	public UserProcessingService(MemberRepository memberRepo, UserRepository userRepo) {
		super(UserTask.class);

		this.memberRepo = memberRepo;
		this.userRepo = userRepo;
		TransportClient transportClient = HttpTransportClient.getInstance();
		vk = new VkApiClient(transportClient);
		this.logger = LogManager.getLogger(UserProcessingService.class);
	}

	@Override
	protected Consumer getExecutor(Channel rabbitChannel2) {
		return new UserProcessingService.Executor(UserTask.class, rabbitChannel2);
	}

	@Override
	protected String getQueueName() {
		return "users";
	}

	@Override
	protected void processTask(UserTask message) throws Exception {
		Optional<User> existsUser = userRepo.findBySnId(message.getSnUser().getId());
		UUID newIserId = !existsUser.isPresent() ? userRepo.saveAndFlush(toUser(message.getSnUser())).getId()
				: existsUser.get().getId();

		boolean alreadyExists = message.getMembers().stream().anyMatch(x -> x.equals(message.getGroupId())
						&& x.getMemberId().getUserId().equals(newIserId));
		if (alreadyExists)
			return;

		Member newMember = new Member();
		newMember.setMemberId(new MemberIdentity(message.getGroupId(), newIserId));
		newMember.setJoinTime(System.currentTimeMillis() / 1000L);

		memberRepo.saveAndFlush(newMember);
	}

	private User toUser(UserXtrRole vkUser) {
		final UUID newId = UUID.randomUUID();
		User user = new User();
		user.setId(newId);
		user.setSnId(vkUser.getId());
		user.setFirstName(vkUser.getFirstName());
		user.setLastName(vkUser.getLastName());
		if (vkUser.getSex() != null)
			user.setSex(vkUser.getSex().getValue());
		else
			user.setSex(0);
		user.setBdate(vkUser.getBdate());
		user.setRelation(vkUser.getRelation());
		user.setPhoto50(vkUser.getPhoto50());
		if (vkUser.getCountry() != null)
			user.setCountryId(vkUser.getCountry().getId());
		if (vkUser.getCity() != null)
			user.setCityId(vkUser.getCity().getId());
		user.setCanWritePrivateMessage(vkUser.canWritePrivateMessage());
		user.setCanSendFriendRequest(vkUser.canSendFriendRequest());
		return user;
	}

	public class Executor extends QueueService.Executor<UserTask> {

		public Executor(Class<UserTask> typeParameterClass, Channel channel) {
			super(typeParameterClass, UserProcessingService.this, channel);
		}

	}

}

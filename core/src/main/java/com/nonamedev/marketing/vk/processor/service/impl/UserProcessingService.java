package com.nonamedev.marketing.vk.processor.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nonamedev.marketing.vk.processor.config.Config;
import com.nonamedev.marketing.vk.processor.datalayer.Member;
import com.nonamedev.marketing.vk.processor.datalayer.MemberIdentity;
import com.nonamedev.marketing.vk.processor.datalayer.User;
import com.nonamedev.marketing.vk.processor.repository.MemberRepository;
import com.nonamedev.marketing.vk.processor.repository.UserRepository;
import com.nonamedev.marketing.vk.processor.tasks.UserTask;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.vk.api.sdk.objects.groups.UserXtrRole;

@Service
public class UserProcessingService extends QueueService<UserTask> {

	private final MemberRepository memberRepo;
	private final UserRepository userRepo;

	public UserProcessingService(MemberRepository memberRepo, UserRepository userRepo, Config config) {
		super(config);

		this.memberRepo = memberRepo;
		this.userRepo = userRepo;
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
		List<Long> userIds = message
				.getNewUsers()
				.stream()
				.map(x -> (long) x.getId())
				.collect(Collectors.toList());
		List<Long> existsUserIds = userRepo
				.findAllById(userIds)
				.stream()
				.map(x -> x.getId())
				.collect(Collectors.toList());
		List<User> newUsers = message
				.getNewUsers()
				.stream()
				.filter(x -> !existsUserIds.contains((long) x.getId()))
				.map(x -> toUser(x))
				.collect(Collectors.toList());
		newUsers = userRepo.saveAll(newUsers);

		long joinTime = System.currentTimeMillis() / 1000L;
		List<Long> existsMembers = memberRepo
				.findByMemberIdGroupId(message.getGroupId())
				.stream()
				.map(x -> x.getMemberId().getUserId())
				.collect(Collectors.toList());
		List<Member> newMembers = userIds
				.stream()
				.filter(x -> !existsMembers.contains(x))
				.map(id -> Member
						.builder()
						.memberId(new MemberIdentity(message.getGroupId(), id))
						.joinTime(joinTime)
						.build())
				.collect(Collectors.toList());
		newMembers = memberRepo.saveAll(newMembers);
	}

	private User toUser(UserXtrRole vkUser) {
		User user = new User();
		user.setId(vkUser.getId());
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

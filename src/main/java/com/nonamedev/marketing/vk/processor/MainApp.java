package com.nonamedev.marketing.vk.processor;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.nonamedev.marketing.vk.processor.datalayer.Group;
import com.nonamedev.marketing.vk.processor.datalayer.Member;
import com.nonamedev.marketing.vk.processor.datalayer.MemberIdentity;
import com.nonamedev.marketing.vk.processor.datalayer.User;
import com.nonamedev.marketing.vk.processor.repository.GroupRepository;
import com.nonamedev.marketing.vk.processor.repository.MemberRepository;
import com.nonamedev.marketing.vk.processor.repository.UserRepository;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.GroupFull;
import com.vk.api.sdk.objects.groups.UserXtrRole;
import com.vk.api.sdk.queries.groups.GroupField;
import com.vk.api.sdk.queries.groups.GroupsGetMembersQueryWithFields;
import com.vk.api.sdk.queries.users.UserField;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MainApp implements ApplicationRunner {
	public static final AppSettings Settings = new AppSettings();

	private final Logger logger = LogManager.getLogger(MainApp.class);
	private long joinTime;

    private final GroupRepository groupRepo;
    private final UserRepository userRepo;
    private final MemberRepository memberRepo;

	public void run(ApplicationArguments args) throws Exception {
		joinTime = System.currentTimeMillis() / 1000L;

		AppSettingsManager.loadConfig();
		/*
		QueueManager.init();
		QueueManager.send("123");
		*/
		
		/*
		Group groupNew = Group.builder()
				.id(UUID.randomUUID())
				.caption("ArtPlatinum Show - огненное и световое шоу")
				.snId(82781623)
				.snName("art_platinumshow")
				.build();
		groupRepo.saveAndFlush(groupNew);
		*/

		TransportClient transportClient = HttpTransportClient.getInstance();
		VkApiClient vk = new VkApiClient(transportClient);

		List<Group> groups = groupRepo.findAll();
		groups.parallelStream().forEach(group -> {
			try {
				processGroup(vk, group);
			} catch (Exception e) {
				logger.error(MessageFormat.format("Error on process group ({0}): {1}", group.getSnId(), e.getMessage()));
			}
		});
		long finishTime = System.currentTimeMillis() / 1000L;
		long processingTime = finishTime - joinTime;
		logger.info(MessageFormat.format("Processing time: {0} secs", Long.toString(processingTime)));
	}

	private void processGroup(VkApiClient vk, Group group) throws SQLException, PropertyVetoException, ApiException, ClientException {

	}

	private void processUser(UserXtrRole vkUser, Group group, List<Member> existsMembers) throws SQLException, PropertyVetoException {
		Optional<User> existsUser = userRepo.findBySnId(vkUser.getId());
		UUID newIserId = !existsUser.isPresent() ? userRepo.saveAndFlush(toUser(vkUser)).getId() : existsUser.get().getId();

		if (existsMembers.stream().anyMatch(x -> x.getMemberId().getGroupId().equals(group.getId()) && x.getMemberId().getUserId().equals(newIserId)))
			return;

		Member newMember = new Member();
		newMember.setMemberId(new MemberIdentity(group.getId(), newIserId));
		newMember.setJoinTime(joinTime);

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
}

package com.nonamedev.marketing.vk.processor;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nonamedev.marketing.vk.processor.datalayer.Group;
import com.nonamedev.marketing.vk.processor.datalayer.GroupsDAO;
import com.nonamedev.marketing.vk.processor.datalayer.Member;
import com.nonamedev.marketing.vk.processor.datalayer.MembersDAO;
import com.nonamedev.marketing.vk.processor.datalayer.User;
import com.nonamedev.marketing.vk.processor.datalayer.UsersDAO;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.GroupFull;
import com.vk.api.sdk.objects.groups.UserXtrRole;
import com.vk.api.sdk.queries.groups.GroupField;
import com.vk.api.sdk.queries.groups.GroupsGetMembersQueryWithFields;
import com.vk.api.sdk.queries.users.UserField;

public class MainApp {
	public static final AppSettings Settings = new AppSettings();

	private static final Logger logger = LogManager.getLogger(MainApp.class);
	private static long joinTime;

	public static void main(String[] args) throws ApiException, ClientException, SQLException, PropertyVetoException, IOException {
		joinTime = System.currentTimeMillis() / 1000L;

		AppSettingsManager.loadConfig();
		QueueManager.init();
		QueueManager.send("123");

		TransportClient transportClient = HttpTransportClient.getInstance();
		VkApiClient vk = new VkApiClient(transportClient);

		List<Group> groups = GroupsDAO.getInstance().getGroups();
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

	private static void processGroup(VkApiClient vk, Group group) throws SQLException, PropertyVetoException, ApiException, ClientException {
		List<GroupFull> vkGroups = vk.groups().getById().groupId(Long.toString(group.getSnId())).fields(GroupField.MEMBERS_COUNT).execute();
		if (vkGroups.size() == 0)
			return;

		List<Member> existsMembers = MembersDAO.getInstance().getMembers(group.getId());
		UserField[] fields = new UserField[] { UserField.SEX, UserField.BDATE, UserField.RELATION, UserField.PHOTO_50, UserField.COUNTRY, UserField.CITY, UserField.CAN_WRITE_PRIVATE_MESSAGE, UserField.CAN_SEND_FRIEND_REQUEST };
		IntFunction<List<UserXtrRole>> mapper = new IntFunction<List<UserXtrRole>>() {
			@Override
			public List<UserXtrRole> apply(int value) {
				try {
					GroupsGetMembersQueryWithFields request = vk.groups().getMembers(fields).groupId(Long.toString(group.getSnId())).offset(value * 1000);
					return request.execute().getItems();
				} catch (Exception e) {
					logger.error(MessageFormat.format("Error on request members from VK ({0} : {1}-{2}): {3}", group.getSnId(), (value * 1000), ((value + 1) * 1000), e.getMessage()));
					return new ArrayList<UserXtrRole>();
				}
			}
		};

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

		List<UserXtrRole> vkMembers = IntStream
			.rangeClosed(0, vkGroups.get(0).getMembersCount() / 1000)
			.parallel()
			.mapToObj(mapper)
			.flatMap(List::stream)
			.collect(Collectors.toList());

		vkMembers.parallelStream().forEach(action);
	}

	private static void processUser(UserXtrRole vkUser, Group group, List<Member> existsMembers) throws SQLException, PropertyVetoException {
		User existsUser = UsersDAO.getInstance().get(vkUser.getId());
		UUID newIserId = existsUser == null ? UsersDAO.getInstance().insert(toUser(vkUser)) : existsUser.getId();

		if (existsMembers.stream().anyMatch(x -> x.getGroupId().equals(group.getId()) && x.getUserId().equals(newIserId)))
			return;

		Member newMember = new Member();
		newMember.setGroupId(group.getId());
		newMember.setUserId(newIserId);
		newMember.setJoinTime(joinTime);

		MembersDAO.getInstance().insert(newMember);
	}

	private static User toUser(UserXtrRole vkUser) {
		User user = new User();
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

package com.nonamedevelopment.marketing.vk.processor;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import com.nonamedevelopment.marketing.vk.processor.datalayer.Group;
import com.nonamedevelopment.marketing.vk.processor.datalayer.GroupsDAO;
import com.nonamedevelopment.marketing.vk.processor.datalayer.Member;
import com.nonamedevelopment.marketing.vk.processor.datalayer.MembersDAO;
import com.nonamedevelopment.marketing.vk.processor.datalayer.User;
import com.nonamedevelopment.marketing.vk.processor.datalayer.UsersDAO;
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

public class App {
	public static void main(String[] args) throws ApiException, ClientException, SQLException, PropertyVetoException {
		TransportClient transportClient = HttpTransportClient.getInstance();
		VkApiClient vk = new VkApiClient(transportClient);

		List<Group> groups = GroupsDAO.getInstance().getGroups();
		groups.parallelStream().forEach(group -> {
			try {
				processGroup(vk, group);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
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
					e.printStackTrace();
					return new ArrayList<UserXtrRole>();
					//throw new RuntimeException(e);
				}
			}
		};

		Consumer<UserXtrRole> action = new Consumer<UserXtrRole>() {
			@Override
			public void accept(UserXtrRole vkUser) {
				try {
					processUser(vkUser, group, existsMembers);
				} catch (Exception e) {
					e.printStackTrace();
					//throw new RuntimeException(e);
				}
			}
		};

		IntStream
			.rangeClosed(0, vkGroups.get(0).getMembersCount() / 1000)
			.parallel()
			.mapToObj(mapper)
			.flatMap(List::stream)
			.forEach(action);
	}

	private static void processUser(UserXtrRole vkUser, Group group, List<Member> existsMembers) throws SQLException, PropertyVetoException {
		long joinTime = 1488629204L;// System.currentTimeMillis() / 1000;
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

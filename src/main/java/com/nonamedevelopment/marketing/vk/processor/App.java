package com.nonamedevelopment.marketing.vk.processor;

import java.util.List;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.UserXtrRole;
import com.vk.api.sdk.queries.users.UserField;

public class App {
	public static void main(String[] args) throws ApiException, ClientException {
		TransportClient transportClient = HttpTransportClient.getInstance();
		VkApiClient vk = new VkApiClient(transportClient);
		UserField[] fields = new UserField[] { UserField.SEX, UserField.BDATE, UserField.RELATION, UserField.PHOTO_50, UserField.COUNTRY, UserField.CITY, UserField.CAN_WRITE_PRIVATE_MESSAGE, UserField.CAN_SEND_FRIEND_REQUEST };

		int offset = 0;
		List<UserXtrRole> members = null;
		do {
			members = vk.groups().getMembers(fields).groupId("38369814").offset(offset).execute().getItems();
			for (UserXtrRole user : members) {
				System.out.println(String.format("%10d (%10s) = %s %s", user.getId(), user.getBdate(), user.getFirstName(), user.getLastName()));
			}
			offset += members.size(); 
		} while (members.size() == 1000);
	}
}

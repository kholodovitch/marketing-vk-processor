package com.nonamedev.marketing.vk.processor.tasks;

import java.util.List;
import java.util.UUID;

import com.nonamedev.marketing.vk.processor.datalayer.Member;
import com.vk.api.sdk.objects.groups.UserXtrRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTask {

	private UserXtrRole snUser;
	
	private UUID groupId;
	
	private List<Member> members;

}

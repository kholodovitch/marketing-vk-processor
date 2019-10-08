package com.nonamedev.marketing.vk.processor.tasks;

import java.util.List;

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

	private List<UserXtrRole> newUsers;

	private long groupId;

}

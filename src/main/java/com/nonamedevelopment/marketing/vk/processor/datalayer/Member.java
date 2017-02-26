package com.nonamedevelopment.marketing.vk.processor.datalayer;

import java.util.UUID;

public class Member {
	private UUID groupId;
	private UUID userId;
	private long joinTime;

	public UUID getGroupId() {
		return groupId;
	}

	public void setGroupId(UUID groupId) {
		this.groupId = groupId;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public long getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(long joinTime) {
		this.joinTime = joinTime;
	}

}

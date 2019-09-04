package com.nonamedev.marketing.vk.processor.datalayer;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

	private UUID groupId;
	private UUID userId;
	private long joinTime;

}

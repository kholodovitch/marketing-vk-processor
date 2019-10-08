package com.nonamedev.marketing.vk.processor.api.v1.rest.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupDto {

	private UUID id;
	private long snId;
	private String snName;
	private String caption;

}

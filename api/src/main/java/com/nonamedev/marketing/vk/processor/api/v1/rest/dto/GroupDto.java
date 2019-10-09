package com.nonamedev.marketing.vk.processor.api.v1.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupDto {

	private long snId;
	private String snName;
	private String caption;

}

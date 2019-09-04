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
public class Group {

	private UUID id;
	private long snId;
	private String snName;
	private String caption;

}

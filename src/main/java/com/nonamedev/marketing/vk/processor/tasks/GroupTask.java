package com.nonamedev.marketing.vk.processor.tasks;

import java.util.UUID;

import com.nonamedev.marketing.vk.processor.datalayer.Group;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroupTask {

	private UUID id;
	private long snId;

}

package com.nonamedev.marketing.vk.processor.service.impl;

import org.springframework.stereotype.Component;

import com.nonamedev.marketing.vk.processor.api.v1.rest.dto.GroupDto;
import com.nonamedev.marketing.vk.processor.datalayer.Group;
import com.nonamedev.marketing.vk.processor.repository.GroupRepository;
import com.nonamedev.marketing.vk.processor.service.ProcessorService;
import com.nonamedev.marketing.vk.processor.tasks.GroupTask;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcessorServiceImpl implements ProcessorService {
	private final GroupProcessingService groupProcessingService;
	private final GroupRepository groupRepo;

	@Override
	public GroupDto createGroup(GroupDto group) {
		Group groupNew = Group
				.builder()
				.caption(group.getCaption())
				.snId(group.getSnId())
				.snName(group.getSnName())
				.build();
		groupNew = groupRepo.saveAndFlush(groupNew);

		GroupTask groupTask = GroupTask
				.builder()
				.snId(group.getSnId())
				.id(groupNew.getId())
				.build();
		groupProcessingService.send(groupTask);

		return new GroupDto(
				groupNew.getId(),
				groupNew.getSnId(),
				groupNew.getSnName(),
				groupNew.getCaption());
	}

}

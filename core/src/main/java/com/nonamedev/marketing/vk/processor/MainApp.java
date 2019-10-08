package com.nonamedev.marketing.vk.processor;

import java.util.UUID;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.nonamedev.marketing.vk.processor.datalayer.Group;
import com.nonamedev.marketing.vk.processor.repository.GroupRepository;
import com.nonamedev.marketing.vk.processor.services.GroupProcessingService;
import com.nonamedev.marketing.vk.processor.tasks.GroupTask;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MainApp implements ApplicationRunner {
	private final GroupProcessingService groupProcessingService;
	private final GroupRepository groupRepo;

	public void run(ApplicationArguments args) throws Exception {
		Group groupNew;
		if (groupRepo.count() == 0) {
			groupNew = Group
					.builder()
					.id(UUID.randomUUID())
					.caption("ArtPlatinum Show - огненное и световое шоу")
					.snId(82781623)
					.snName("art_platinumshow")
					.build();
			groupNew = groupRepo.saveAndFlush(groupNew);
		} else {
			groupNew = groupRepo.findAll().get(0);
		}
		
		GroupTask groupTask = GroupTask.builder().snId(groupNew.getSnId()).id(groupNew.getId()).build();
		groupProcessingService.send(groupTask);
	}
}

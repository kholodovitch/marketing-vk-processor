package com.nonamedev.marketing.vk.processor.api.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nonamedev.marketing.vk.processor.api.v1.rest.ProcessorControllerV1;
import com.nonamedev.marketing.vk.processor.api.v1.rest.dto.GroupDto;
import com.nonamedev.marketing.vk.processor.service.ProcessorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProcessorControllerV1Impl implements ProcessorControllerV1 {

	private final ProcessorService processorService;

	@Override
	public ResponseEntity<GroupDto> createGroup(GroupDto group) {
		return ResponseEntity.ok(processorService.createGroup(group));
	}

}

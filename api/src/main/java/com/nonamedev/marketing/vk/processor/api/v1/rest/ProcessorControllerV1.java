package com.nonamedev.marketing.vk.processor.api.v1.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nonamedev.marketing.vk.processor.api.v1.rest.dto.GroupDto;

@FeignClient(name = "vk-processor", url = "${feign.url.vk-processor}")
public interface ProcessorControllerV1 {

	@PostMapping(value = "/api/v1/groups")
	ResponseEntity<GroupDto> createGroup(@RequestBody GroupDto group);

	@PostMapping(value = "/api/v1/groups/process")
	ResponseEntity<Void> processGroups(@RequestBody Long[] groupIds);

}

package com.nonamedev.marketing.vk.processor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nonamedev.marketing.vk.processor.datalayer.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {

}

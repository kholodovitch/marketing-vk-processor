package com.nonamedev.marketing.vk.processor.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nonamedev.marketing.vk.processor.datalayer.Group;

public interface UserRepository extends JpaRepository<Group, UUID> {

}

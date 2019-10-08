package com.nonamedev.marketing.vk.processor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nonamedev.marketing.vk.processor.datalayer.User;

public interface UserRepository extends JpaRepository<User, Long> {

}

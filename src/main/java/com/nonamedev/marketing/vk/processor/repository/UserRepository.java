package com.nonamedev.marketing.vk.processor.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nonamedev.marketing.vk.processor.datalayer.User;

public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findBySnId(Integer id);

}

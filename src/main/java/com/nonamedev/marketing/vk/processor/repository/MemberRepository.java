package com.nonamedev.marketing.vk.processor.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nonamedev.marketing.vk.processor.datalayer.Member;
import com.nonamedev.marketing.vk.processor.datalayer.MemberIdentity;

public interface MemberRepository extends JpaRepository<Member, MemberIdentity> {

	List<Member> findAllByMemberIdGroupId(UUID groupId);

}

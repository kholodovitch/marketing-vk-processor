package com.nonamedev.marketing.vk.processor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nonamedev.marketing.vk.processor.datalayer.Member;
import com.nonamedev.marketing.vk.processor.datalayer.MemberIdentity;

public interface MemberRepository extends JpaRepository<Member, MemberIdentity> {

	boolean existsByMemberIdGroupIdAndMemberIdUserId(long groupId, long newUserId);

}

package com.nonamedev.marketing.vk.processor.datalayer;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "vk_member")
public class Member {

	@EmbeddedId
	private MemberIdentity memberId;

	private long joinTime;

}

package com.nonamedev.marketing.vk.processor.datalayer;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MemberIdentity implements Serializable {

	private static final long serialVersionUID = 5876115305682553726L;

	private long groupId;

	private long userId;

}

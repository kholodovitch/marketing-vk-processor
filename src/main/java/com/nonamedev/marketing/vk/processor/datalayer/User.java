package com.nonamedev.marketing.vk.processor.datalayer;

import java.util.UUID;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	private UUID id;
	private int snId;
	private String firstName;
	private String lastName;
	private String bdate;
	private int sex;
	private Integer relation;
	private String photo50;
	private Integer countryId;
	private Integer cityId;
	private boolean canWritePrivateMessage;
	private boolean canSendFriendRequest;

}

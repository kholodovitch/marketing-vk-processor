package com.nonamedev.marketing.vk.processor.datalayer;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Table(name = "vk_user")
public class User {

	@Id
	@GeneratedValue
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

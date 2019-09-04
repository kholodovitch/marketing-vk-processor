package com.nonamedev.marketing.vk.processor.datalayer;

import java.util.UUID;

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

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public int getSnId() {
		return snId;
	}

	public void setSnId(int snId) {
		this.snId = snId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getBdate() {
		return bdate;
	}

	public void setBdate(String bdate) {
		this.bdate = bdate;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public Integer getRelation() {
		return relation;
	}

	public void setRelation(Integer relation) {
		this.relation = relation;
	}

	public String getPhoto50() {
		return photo50;
	}

	public void setPhoto50(String photo50) {
		this.photo50 = photo50;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public boolean isCanWritePrivateMessage() {
		return canWritePrivateMessage;
	}

	public void setCanWritePrivateMessage(boolean canWritePrivateMessage) {
		this.canWritePrivateMessage = canWritePrivateMessage;
	}

	public boolean isCanSendFriendRequest() {
		return canSendFriendRequest;
	}

	public void setCanSendFriendRequest(boolean canSendFriendRequest) {
		this.canSendFriendRequest = canSendFriendRequest;
	}

}

package com.nonamedevelopment.marketing.vk.processor.datalayer;

import java.util.UUID;

public class Group {
	private UUID id;
	private long snId;
	private String snName;
	private String caption;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public long getSnId() {
		return snId;
	}

	public void setSnId(long snId) {
		this.snId = snId;
	}

	public String getSnName() {
		return snName;
	}

	public void setSnName(String snName) {
		this.snName = snName;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
}

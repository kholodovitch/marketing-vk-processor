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
@Table(name = "vk_group")
public class Group {

	@Id
	@GeneratedValue
	private UUID id;
	private long snId;
	private String snName;
	private String caption;

}

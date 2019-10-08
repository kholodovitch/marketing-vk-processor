package com.nonamedev.marketing.vk.processor.datalayer;

import javax.persistence.Entity;
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
	private long id;
	private String snName;
	private String caption;

}

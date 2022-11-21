package com.nagarro.dataenterpriseplatform.main.db.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dep_stream_metadata")
public class DepStreamMetadataEntity {

	@Id
	@Column
	private String stream_name;

	@Column
	private String id;

	@Column(unique = true)
	private String stream_id = String.valueOf(UUID.randomUUID());

	@Column(name = "client_name")
	private String client_name;

	@Column(name = "table_name")
	private String table_name;

	@Column(name = "rule_engine")
	private boolean ruleEngine;

	@Column
	private boolean storage;

	@Column(name = "flow_builder")
	private boolean flowBuilder;

	@Column
	private String bucket;

	@Column(name = "region")
	private String region;

	@Column(name = "job_file_prefix")
	private String jobFilePrefix;

	@Column
	private String processing;

}

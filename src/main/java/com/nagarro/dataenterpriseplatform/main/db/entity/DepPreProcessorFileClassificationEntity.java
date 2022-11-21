package com.nagarro.dataenterpriseplatform.main.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dep_preprocessor_file_classification")
public class DepPreProcessorFileClassificationEntity {

	@Id
	@Column(unique = true)
	private String id;

	@Column
	private String batch_name;

	@Column
	private String client_name;

	@Column(name = "disable_job")
	private String disableJob;

	@Column
	private String extension;

	@Column(name = "file_destination")
	private String fileDestination;

	@Column(columnDefinition = "TEXT")
	private String key;

	@Column(columnDefinition = "TEXT")
	private String pattern;

	@Column(name = "skip_preprocess")
	private String skip_PreProcess;

	@Column
	private String table_name;

}

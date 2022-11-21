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

import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dep_preprocessor_file_classification_status")
public class DepPreProcessorFileClassificationStatusEntity implements Comparable<DepPreProcessorFileClassificationStatusEntity> {

	@Id
	@Column(unique = true, columnDefinition = "VARCHAR(255)")
	private String id;
	
	@Column
	private String batch_name;

	@Column
	private String client_name;

	@Column(columnDefinition = "TEXT")
	private String destination_bucket;

	@Column(columnDefinition = "TEXT")
	private String destination_key;

	@Column
	private String extension;

	@Column(name = "file_destination", columnDefinition = "TEXT")
	private String fileDestination;

	@Column(columnDefinition = "TEXT")
	private String key;

	@Column(columnDefinition = "TEXT")
	private String operation_performed;

	@Column(columnDefinition = "TEXT")
	private String params;

	@Column(columnDefinition = "TEXT")
	private String row_count;

	@Column
	private String size;

	@Column
	private String status;

	@Column
	private String table_name;

	@Column
	private BigInteger timestamp_id;

	@Override
	public int compareTo(DepPreProcessorFileClassificationStatusEntity arg0) {
		if(this.getId()==null || arg0.getId()==null) {
			return 0;
		}
		return this.getId().compareTo(arg0.getId());
	}

}

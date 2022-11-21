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
@Table(name = "cdep_json_path")
public class CdepJsonPathEntity {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(unique = true, columnDefinition = "VARCHAR(36)")
	private String id;

	@Column
	private String file_path;

	@Column
	private String bucket_name;

	@Column
	private String table_name;

	@Column
	private String batch_name;

	@Column
	private String client_id;

}

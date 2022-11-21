package com.nagarro.dataenterpriseplatform.main.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dep_custom_rule")

public class DepCustomRuleEntity {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(unique = true, columnDefinition = "VARCHAR(36)")
	private String id;

	@Column
	private String client_id;

	@Column
	private String batch_id;

	@Column
	private String rulename;

	@Column
	private String argvalue;

	@Column
	private String argkey;

	@Column
	private String type;
}

package com.nagarro.dataenterpriseplatform.main.db.entity;

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
@Table(name = "dep_rule_engine_job_status")
public class DepRuleEngineJobStatusEntity implements Comparable<DepRuleEngineJobStatusEntity> {
	
	@Id
	@Column(name = "execution_id")
	private String id;

	@Column
	private String client_id;

	@Column
	private String client_name;

	@Column
	private String table_name;

	@Column(columnDefinition = "TEXT")
	private String error_detail;

	@Column
	private String status;

	@Column
	private String batch_id;

	@Column
	private String batch_name;

	@Column
	private String start_time;


	private String end_time;


	@Override
	public int compareTo(DepRuleEngineJobStatusEntity arg0) {
		if (getEnd_time() == null || arg0.getEnd_time() == null) {
			return 0;
		}
		return getEnd_time().compareTo(arg0.getEnd_time());
	}
}

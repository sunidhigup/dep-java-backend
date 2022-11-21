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
@Table(name = "dep_flowbuilder_job_status")
public class DepFlowbuilderJobStatusEntity implements Comparable<DepFlowbuilderJobStatusEntity> {

	@Id
	@Column
	private String execution_id;

	@Column
	private String batch_id;

	@Column
	private String batch_name;

	@Column
	private String client_id;

	@Column
	private String client_name;

	@Column
	private String end_time;

	@Column(columnDefinition = "TEXT")
	private String error_description;

	@Column(columnDefinition = "TEXT")
	private String error_detail;

	@Column(columnDefinition = "TEXT")
	private String extract;

	@Column
	private String start_time;

	@Column
	private String status;

	@Column
	private String step_name;

	@Column
	private String step_number;

	@Column
	private String job_name;

	@Override
	public int compareTo(DepFlowbuilderJobStatusEntity u) {
		if (getEnd_time() == null || u.getEnd_time() == null) {
			return 0;
		}
		return getEnd_time().compareTo(u.getEnd_time());
	}
}

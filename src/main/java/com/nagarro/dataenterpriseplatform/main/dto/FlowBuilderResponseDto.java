package com.nagarro.dataenterpriseplatform.main.dto;

import lombok.Data;

@Data
public class FlowBuilderResponseDto {

	private String job_id;
	private String input_ref_key;
	private String connectionName;
	private String connectionType;
	private String job_status;
	private Boolean active;
	private String execution_id;
}

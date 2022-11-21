package com.nagarro.dataenterpriseplatform.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobInputExtractUpdateDto {

	private String client_id;
	private String client_name;
	private String batch_id;
	private String batch_name;
	private String job_name;
	private String tracking_id;
	private String filename;

}

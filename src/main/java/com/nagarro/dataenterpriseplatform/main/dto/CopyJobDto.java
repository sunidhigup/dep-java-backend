package com.nagarro.dataenterpriseplatform.main.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CopyJobDto {

	private String client_name;
	private String batch_name;
	private String input_ref_key;
	private String copyBatch;
	private String copyJob;
	private String copyClientId;
	private String copyBatchId;
	private String client_id;
	private String batch_id;
}

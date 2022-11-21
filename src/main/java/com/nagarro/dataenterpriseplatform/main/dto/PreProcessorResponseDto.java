package com.nagarro.dataenterpriseplatform.main.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreProcessorResponseDto {

	private String id;
	private String disableJob;
	private String extension;
	private String table_name;
	private String status;
	private String batch_name;
	private String client_name;
	private String fileDestination;
	private String key;
	private String pattern;
	private String skip_PreProcess;
	private String execution_id;
}

package com.nagarro.dataenterpriseplatform.main.dto;

import java.util.List;

import lombok.Data;

@Data
public class RuleEngineResponseDto {
	
	private String id;
	private String tablename;
	private String batchname;
	private String path;
	private String client_id;
	private Boolean generated;
	private List<TableJsonFieldsDto> fields;
	private String status;
	private String execution_id;
}

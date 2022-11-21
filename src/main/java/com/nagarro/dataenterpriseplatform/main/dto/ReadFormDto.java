package com.nagarro.dataenterpriseplatform.main.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadFormDto {

	private String nodeId;
	private String nodeName;
	private FormDataDto formField;
	private Boolean disabled;
	private String step_name;
	private int y_axis;

	private List<Map<String, String>> fetchedHeader;
    private List<Map<String,String>> udfList;
	private List<Map<String, String>> headerName;
	private Map<String, String> count;
	private List<Map<String, String>> onRow;
	private List<Map<String, String>> sorting;
	private List<CleansingRulesDto> cleansingRules;
	private List<Map<String, String>> customRules;
	private String table_name;
	private String client_name;
	private String batch_name;
}

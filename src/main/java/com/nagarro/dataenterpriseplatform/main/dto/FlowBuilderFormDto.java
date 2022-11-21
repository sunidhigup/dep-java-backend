package com.nagarro.dataenterpriseplatform.main.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowBuilderFormDto implements Serializable {

	private String step_name;
	private int y_axis;
	private String nodeId;
	private String nodeName;
	private Boolean disabled;
	private List<Map<String, String>> fetchedHeader;
	private List<Map<String, String>> headerName;
	private Map<String, String> count;
	private List<Map<String, String>> onRow;
	private List<Map<String, String>> sorting;
	private List<Map<String, String>> aggregate;
	private List<String> groupBy;
	private List<CleansingRulesDto> cleansingRules;
	private List<CleansingRulesDto> initial_rules;
	private List<Map<String, String>> customRules;
	private String toggleType;
	private String table_name;
	private String client_name;
	private String batch_name;
	private FormDataDto formField;
}

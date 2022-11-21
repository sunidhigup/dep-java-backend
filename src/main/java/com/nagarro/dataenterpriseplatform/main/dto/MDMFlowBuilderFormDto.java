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
public class MDMFlowBuilderFormDto implements Serializable {

	private String step_name;
	private int y_axis;
	private String nodeId;
	private String nodeName;
	private Boolean disabled;

	private MDMEntityMetadataDto selectedEntity;
	private List<Map<String, String>> fetchedHeader;
	private MDMFormDataDto formField;

	private List<Map<String, String>> exactMatch;
	private List<Map<String, String>> fuzzyMatch;
	private List<Map<String, String>> attribute;
	private List<String> tempMapping;
}

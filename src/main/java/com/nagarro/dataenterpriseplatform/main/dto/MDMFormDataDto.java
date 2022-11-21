package com.nagarro.dataenterpriseplatform.main.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MDMFormDataDto {

	private String path;
	private String format;
	private String alias;
	private String source;
	private String sourceId;
	private String entity;
	private String delimiter;
	private List<Map<String, String>> mapping;
	private List<String> fuzzy_match;
	private List<String> exact_match;
	private int threshold;
	private String algo;
	private boolean persist;
	private String persist_type;
	private boolean partition;
	private boolean overwrite;
	private String df;
}

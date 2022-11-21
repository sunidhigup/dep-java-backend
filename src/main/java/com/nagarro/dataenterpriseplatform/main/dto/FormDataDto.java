package com.nagarro.dataenterpriseplatform.main.dto;

import java.util.Map;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormDataDto {

	private int step_no;
	private Boolean partition;
	private Boolean overwrite;
	private Boolean persist;
	private String persist_type;
	private String db_name;
	private String df;
	private Boolean ignoreblanklines;
	private Boolean skipheaders;
	private Boolean columnshift;
	private Boolean junkrecords;
	private Boolean linebreak;
	private String segment;
	private Boolean otherBlocksConnected;
	private String alias;
	private String action;
	private String statement;
	private String format;
	private String path;
	private Map<String, String> joins;
	private String join_conditions;
	private String select_cols;
	private String join_filter;
	private Map<String, String> tables;
	private String database;
	private String tablename;
	private String delimiter;


	// private String bucket;
	private String clientId;
	private String clientName;
	private String batchName;
	private String tableNameRead;
	private String trackingId;
	private String innerPath;
	private String file;
	private String dataTypeRead;
	private String index;
	private String p_key;
	private String db_type;
	private String mode;


}

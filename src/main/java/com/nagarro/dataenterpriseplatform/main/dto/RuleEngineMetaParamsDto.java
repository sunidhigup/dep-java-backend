package com.nagarro.dataenterpriseplatform.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleEngineMetaParamsDto implements Serializable {

	private String batch_name;
	private String bucket_name;
	private String client_name;
	private String table_name;
	private String delimiter;
	private String region_name;
	private String jar_folder_path;
	private String file_extension;
	private String db_status_table;
	private String db_audit_table;
	private String log_group;
}

package com.nagarro.dataenterpriseplatform.main.dto;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CleansingRulesDto {

	private String header;
	private String type;
	private int scale;
	private int size;
	private List<String> rulename;
	private Boolean deleted;
	private Boolean checked;
	private Boolean selected;
}

package com.nagarro.dataenterpriseplatform.main.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableJsonDto {

	private String jsonversion;
	private String revision;
	private String filetype;
	private String delimiter;
	private List<TableJsonFieldsDto> fields;
}

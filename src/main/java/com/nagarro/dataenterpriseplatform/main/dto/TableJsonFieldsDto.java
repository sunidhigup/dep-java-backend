package com.nagarro.dataenterpriseplatform.main.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableJsonFieldsDto implements Serializable {

	private String fieldname;
	private Long size;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Long scale;
	private String type;
	private Boolean deleted;

	private List<String> rulename;
}

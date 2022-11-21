package com.nagarro.dataenterpriseplatform.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MDMAttributeMetadataDto {

	private String name;

	private String label;

	private String type;

	private boolean required;

}
